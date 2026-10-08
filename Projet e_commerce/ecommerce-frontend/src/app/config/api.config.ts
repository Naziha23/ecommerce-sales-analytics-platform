export function getApiBaseUrl(): string {
  const host = typeof window !== 'undefined' && window.location ? window.location.hostname : '127.0.0.1';
  return `http://${host}:8081/api`;
}
