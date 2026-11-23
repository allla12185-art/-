const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read, list, write, or delete lessonProgress", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("lessonProgress").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("lessonProgress")
      .doc("a1_greetings_be")
      .get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("lessonProgress")
      .doc("a1_greetings_be")
      .set({
        userId: ALICE_UID,
        levelCode: "A1",
        titleAr: "التحيات وفعل الكينونة",
        completed: true,
        completedAt: new Date(Date.now() - 1000),
        updatedAt: new Date(Date.now() - 1000),
      })
  );
});

test("Authenticated owner can create, list, get, partially update, and delete lessonProgress", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);
  const colRef = aliceDb.collection("users").doc(ALICE_UID).collection("lessonProgress");
  const docRef = colRef.doc("a1_greetings_be");

  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      levelCode: "A1",
      titleAr: "التحيات وفعل الكينونة (Verb to be)",
      completed: true,
      completedAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(colRef.get());
  await assertSucceeds(docRef.get());

  await assertSucceeds(
    docRef.update({
      titleAr: "التحيات وفعل الكينونة - مراجعة",
      completed: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertSucceeds(docRef.delete());
});

test("Owner cannot inject @DocumentId lessonId field, ghost fields, invalid levelCode, oversized title, or future timestamps", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 2000);
  const colRef = aliceDb.collection("users").doc(ALICE_UID).collection("lessonProgress");

  await assertFails(
    colRef.doc("a1_daily_routine").set({
      lessonId: "a1_daily_routine",
      userId: ALICE_UID,
      levelCode: "A1",
      titleAr: "الروتين اليومي",
      completed: true,
      completedAt: now,
      updatedAt: now,
    })
  );

  await assertFails(
    colRef.doc("a1_daily_routine").set({
      userId: ALICE_UID,
      levelCode: "A1",
      titleAr: "الروتين اليومي",
      completed: true,
      isAdmin: true,
      completedAt: now,
      updatedAt: now,
    })
  );

  await assertFails(
    colRef.doc("a1_daily_routine").set({
      userId: ALICE_UID,
      levelCode: "Z9",
      titleAr: "الروتين اليومي",
      completed: true,
      completedAt: now,
      updatedAt: now,
    })
  );

  await assertFails(
    colRef.doc("a1_daily_routine").set({
      userId: ALICE_UID,
      levelCode: "A1",
      titleAr: "أ".repeat(121),
      completed: true,
      completedAt: now,
      updatedAt: now,
    })
  );

  await assertFails(
    colRef.doc("a1_daily_routine").set({
      userId: ALICE_UID,
      levelCode: "A1",
      titleAr: "الروتين اليومي",
      completed: true,
      completedAt: new Date(Date.now() + 60000),
      updatedAt: now,
    })
  );

  await assertSucceeds(
    colRef.doc("a2_past_travel").set({
      userId: ALICE_UID,
      levelCode: "A2",
      titleAr: "الماضي البسيط والسفر",
      completed: true,
      completedAt: now,
      updatedAt: now,
    })
  );
  await assertFails(
    colRef.doc("a2_past_travel").update({
      completedAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Cross-user isolation: Bob cannot read, list, create, update, or delete Alice's lessonProgress", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("users")
      .doc(ALICE_UID)
      .collection("lessonProgress")
      .doc("a1_greetings_be")
      .set({
        userId: ALICE_UID,
        levelCode: "A1",
        titleAr: "التحيات وفعل الكينونة",
        completed: true,
        completedAt: new Date(Date.now() - 10000),
        updatedAt: new Date(Date.now() - 10000),
      });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const aliceCol = bobDb.collection("users").doc(ALICE_UID).collection("lessonProgress");

  await assertFails(aliceCol.get());
  await assertFails(aliceCol.doc("a1_greetings_be").get());
  await assertFails(
    aliceCol.doc("a1_greetings_be").update({ titleAr: "Hijacked", updatedAt: new Date(Date.now() - 500) })
  );
  await assertFails(aliceCol.doc("a1_greetings_be").delete());
  await assertFails(
    aliceCol.doc("b1_present_perfect").set({
      userId: BOB_UID,
      levelCode: "B1",
      titleAr: "Injected",
      completed: true,
      completedAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Users cannot read or write unmodeled root user documents", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).set({ role: "admin" }));
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).get());
});

// --- DAILY WORD ('كلمة اليوم') TESTS ---

test("DailyWord: unauthenticated and cross-user access are denied", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 5000);

  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("users")
      .doc(ALICE_UID)
      .collection("dailyWords")
      .doc("2026-11-23")
      .set({
        userId: ALICE_UID,
        wordEn: "Resilient",
        ipa: "/rɪˈzɪljənt/",
        partOfSpeechAr: "صفة",
        meaningAr: "مرن / قادر على التعافي بسرعة",
        exampleEn: "Consistent learners stay resilient even on busy days.",
        exampleAr: "المتعلّمون المواظبون يبقون مرنين حتى في الأيام المزدحمة.",
        levelCode: "B1",
        practiced: false,
        listenCount: 0,
        createdAt: now,
        updatedAt: now,
      });
  });

  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("dailyWords").doc("2026-11-23").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("dailyWords").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("dailyWords").doc("2026-11-23").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("dailyWords").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("dailyWords")
      .doc("2026-11-23")
      .update({ practiced: true, updatedAt: new Date(Date.now() - 1000) })
  );
});

test("DailyWord: owner create, partial updates (practiced & listenCount), list, and validation rejections", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const colRef = aliceDb.collection("users").doc(ALICE_UID).collection("dailyWords");
  const docRef = colRef.doc("2026-11-23");
  const created = new Date(Date.now() - 10000);

  // Valid Kotlin-aligned create payload (no @DocumentId `dateKey` inside payload)
  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      wordEn: "Resilient",
      ipa: "/rɪˈzɪljənt/",
      partOfSpeechAr: "صفة",
      meaningAr: "مرن / سريع التعافي",
      exampleEn: "Keep going and stay resilient in your English journey.",
      exampleAr: "واصل التقدّم وكن مرناً في رحلتك لتعلّم الإنجليزية.",
      levelCode: "B1",
      practiced: false,
      listenCount: 0,
      createdAt: created,
      updatedAt: created,
    })
  );

  // List & Get succeed for owner
  await assertSucceeds(colRef.get());
  await assertSucceeds(docRef.get());

  // Partial updates (toggle practiced, update listenCount) succeed preserving createdAt
  await assertSucceeds(
    docRef.update({
      practiced: true,
      listenCount: 2,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt modification
  await assertFails(
    docRef.update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost field / @DocumentId injection / negative listenCount / invalid CEFR level
  await assertFails(
    colRef.doc("2026-11-24").set({
      dateKey: "2026-11-24",
      userId: ALICE_UID,
      wordEn: "Eloquent",
      ipa: "/ˈeləkwənt/",
      partOfSpeechAr: "صفة",
      meaningAr: "فصيح اللسان",
      exampleEn: "She gave an eloquent speech.",
      exampleAr: "ألقت خطاباً فصيحاً.",
      levelCode: "C1",
      practiced: false,
      listenCount: 0,
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertFails(
    docRef.update({
      listenCount: -1,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    docRef.update({
      levelCode: "X1",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

// --- POST-LESSON QUIZ QUESTIONS & RESULTS TESTS ---

test("QuizQuestions: unauthenticated/cross-user denied, owner CRUD & where(lessonId) query succeed", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);
  const colRef = aliceDb.collection("users").doc(ALICE_UID).collection("quizQuestions");

  // Valid create (no @DocumentId `id` field)
  await assertSucceeds(
    colRef.doc("q_a1_1").set({
      userId: ALICE_UID,
      lessonId: "a1_greetings_be",
      orderIndex: 0,
      promptAr: "اختر الفعل الصحيح للجملة:",
      englishPrompt: "She ___ happy today.",
      options: ["am", "is", "are"],
      correctIndex: 1,
      explanationAr: "نستخدم is مع المفرد الغائب.",
      levelCode: "A1",
      createdAt: created,
      updatedAt: created,
    })
  );

  // Filtered query matching Kotlin repository
  await assertSucceeds(colRef.where("lessonId", "==", "a1_greetings_be").get());

  // Unauthenticated & cross-user isolation
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("quizQuestions").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("quizQuestions")
      .where("lessonId", "==", "a1_greetings_be")
      .get()
  );

  // Reject out-of-bounds correctIndex, @DocumentId `id` injection, and ghost fields
  await assertFails(
    colRef.doc("q_a1_bad").set({
      id: "q_a1_bad",
      userId: ALICE_UID,
      lessonId: "a1_greetings_be",
      orderIndex: 1,
      promptAr: "اختر الإجابة",
      englishPrompt: "I ___ a student.",
      options: ["am", "is"],
      correctIndex: 0,
      explanationAr: "شرح",
      levelCode: "A1",
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertFails(
    colRef.doc("q_a1_bad2").set({
      userId: ALICE_UID,
      lessonId: "a1_greetings_be",
      orderIndex: 1,
      promptAr: "اختر الإجابة",
      englishPrompt: "I ___ a student.",
      options: ["am", "is"],
      correctIndex: 5,
      explanationAr: "شرح",
      levelCode: "A1",
      createdAt: created,
      updatedAt: created,
    })
  );
});

test("QuizResults: owner create/update/list succeed; cross-user & invalid score bounds denied", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);
  const colRef = aliceDb.collection("users").doc(ALICE_UID).collection("quizResults");
  const docRef = colRef.doc("a1_greetings_be");

  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      lessonTitleAr: "التحيات وفعل الكينونة",
      levelCode: "A1",
      correctCount: 2,
      totalCount: 3,
      scorePercent: 67,
      bestScorePercent: 67,
      attemptCount: 1,
      masteryLabelAr: "فهم جيد جداً",
      passed: true,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(colRef.get());

  // Valid second attempt partial update preserving createdAt
  await assertSucceeds(
    docRef.update({
      correctCount: 3,
      totalCount: 3,
      scorePercent: 100,
      bestScorePercent: 100,
      attemptCount: 2,
      masteryLabelAr: "ممتاز! إتقان تام",
      passed: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Cross-user read/write denied
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("quizResults").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("quizResults").doc("a1_greetings_be").get()
  );

  // Invalid bounds: correctCount > totalCount, @DocumentId `lessonId` injection, or createdAt mutation
  await assertFails(
    docRef.update({
      correctCount: 5,
      totalCount: 3,
      updatedAt: new Date(Date.now() - 300),
    })
  );
  await assertFails(
    docRef.update({
      createdAt: new Date(Date.now() - 300),
      updatedAt: new Date(Date.now() - 300),
    })
  );
  await assertFails(
    colRef.doc("a1_daily_routine").set({
      lessonId: "a1_daily_routine",
      userId: ALICE_UID,
      lessonTitleAr: "الروتين اليومي",
      levelCode: "A1",
      correctCount: 1,
      totalCount: 2,
      scorePercent: 50,
      bestScorePercent: 50,
      attemptCount: 1,
      masteryLabelAr: "يحتاج مراجعة",
      passed: false,
      createdAt: created,
      updatedAt: created,
    })
  );
});
