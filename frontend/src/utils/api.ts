export async function apiFetchData(
  url: string,
  method: string,
  bodyData?: Record<string, any>
): Promise<any> {
  const fullUrl = import.meta.env.DEV
    ? import.meta.env.VITE_API_URL_DEV + url
    : import.meta.env.VITE_API_URL_PROD + url

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

  let responseData
  if (response.headers.get('Content-Type')?.includes('application/json')) {
    responseData = await response.json()
  } else {
    responseData = await response.text()
  }
  return responseData
}
