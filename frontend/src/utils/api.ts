export async function apiFetchData<T>(
  url: string,
  method: string,
  bodyData?: Record<string, any>
): Promise<T> {
  const fullUrl = `http://localhost:8080/${url}`
  const headers = new Headers({
    'Content-Type': 'application/json'
  })

  const options: RequestInit = {
    method,
    headers,
    body: bodyData ? JSON.stringify(bodyData) : undefined
  }

  const response = await fetch(fullUrl, options)

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }

  return response.json() as Promise<T>
}
