const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
export async function api(path, { method = 'GET', body } = {}) {
  const token = localStorage.getItem('token')
  const res = await fetch(BASE + path, {
    method,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  })
  if (!res.ok) throw new Error((await res.json().catch(() => ({}))).message || `Erro ${res.status}`)
  const txt = await res.text()
  return txt ? JSON.parse(txt) : null
}
