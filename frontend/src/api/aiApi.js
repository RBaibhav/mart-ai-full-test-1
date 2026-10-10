export function createAiApi(request) {
  return {
    getRecommendations: (message) =>
      request("/ai/recommendations", {
        method: "POST",
        body: JSON.stringify({ message }),
      }),

    queryKnowledge: (question, category, region) =>
      request("/ai/knowledge", {
        method: "POST",
        body: JSON.stringify({ question, category, region }),
      }),

    sendChat: (message) =>
      request("/ai/chat", {
        method: "POST",
        body: JSON.stringify({ message }),
      }),

    getAudits: () => request("/ai/audit"),
  };
}
