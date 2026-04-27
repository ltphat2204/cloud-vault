import FingerprintJS from '@fingerprintjs/fingerprintjs'

let deviceId: string | null = null

export async function getDeviceId(): Promise<string> {
  if (deviceId) return deviceId

  const fpPromise = FingerprintJS.load()
  const fp = await fpPromise
  const result = await fp.get()
  
  deviceId = result.visitorId
  return deviceId
}
