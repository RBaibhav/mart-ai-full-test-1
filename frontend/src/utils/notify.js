export function notify(message, tone = "info") {
  window.dispatchEvent(
    new CustomEvent("shopsmart:toast", {
      detail: { id: crypto.randomUUID(), message, tone },
    }),
  );
}
