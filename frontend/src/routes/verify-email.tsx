import { createFileRoute, Link } from '@tanstack/react-router'
import { useState, useEffect, useCallback } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import api from '@/lib/axios'
import { LoaderCircle, CircleCheckBig, CircleX } from 'lucide-react'
import { z } from 'zod'

const searchSchema = z.object({
  token: z.string().min(1).optional(),
})

export const Route = createFileRoute('/verify-email')({
  validateSearch: (search) => searchSchema.parse(search),
  component: VerifyEmailPage,
})

function VerifyEmailPage() {
  const { token } = Route.useSearch()
  const [status, setStatus] = useState<'loading' | 'success' | 'error' | 'no-token'>(
    !token ? 'no-token' : 'loading',
  )
  const [error, setError] = useState('')
  const [isTokenExpired, setIsTokenExpired] = useState(false)
  const [resendEmail, setResendEmail] = useState('')
  const [resendLoading, setResendLoading] = useState(false)
  const [resendMessage, setResendMessage] = useState<string | null>(null)

  const verify = useCallback(async () => {
    if (!token) {
      setStatus('no-token')
      return
    }

    setStatus('loading')
    try {
      await api.post('auth/verify', null, { params: { token } })
      setStatus('success')
    } catch (err: any) {
      const message =
        err.response?.data?.message || 'Verification failed. Please try again.'
      setError(message)
      setIsTokenExpired(
        message.toLowerCase().includes('expired'),
      )
      setStatus('error')
    }
  }, [token])

  useEffect(() => {
    verify()
  }, [verify])

  const handleResend = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!resendEmail) return

    setResendLoading(true)
    setResendMessage(null)
    try {
      await api.post('auth/resend-verification', null, {
        params: { email: resendEmail },
      })
      setResendMessage('Verification email has been resent. Check your inbox.')
    } catch (err: any) {
      setResendMessage(
        err.response?.data?.message || 'Failed to resend verification email.',
      )
    } finally {
      setResendLoading(false)
    }
  }

  return (
    <div className="flex min-h-[calc(100vh-200px)] items-center justify-center px-4">
      <div className="island-shell rise-in w-full max-w-md rounded-[2rem] p-8 sm:p-10">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
            {status === 'loading' ? (
              <LoaderCircle size={28} className="animate-spin" />
            ) : status === 'success' ? (
              <CircleCheckBig size={28} className="text-green-600" />
            ) : (
              <CircleX size={28} className="text-red-500" />
            )}
          </div>
          <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">
            {status === 'loading'
              ? 'Verifying Your Email'
              : status === 'success'
                ? 'Email Verified!'
                : 'Verification Failed'}
          </h1>
          <p className="mt-2 text-[var(--sea-ink-soft)]">
            {status === 'loading'
              ? 'Please wait while we verify your account...'
              : status === 'success'
                ? 'Your email has been successfully verified.'
                : status === 'no-token'
                  ? 'Invalid verification link. No token provided.'
                  : error}
          </p>
        </div>

        {status === 'success' && (
          <Button asChild className="w-full h-11 rounded-xl font-bold shadow-md">
            <Link to="/login">Sign In</Link>
          </Button>
        )}

        {(status === 'error' || status === 'no-token') && (
          <div className="space-y-4">
            <Button asChild className="w-full h-11 rounded-xl font-bold shadow-md" variant="outline">
              <Link to="/login">Back to Login</Link>
            </Button>

            {isTokenExpired && (
              <div className="rounded-xl border border-[var(--lagoon)]/20 bg-[var(--lagoon)]/5 p-4">
                <p className="mb-3 text-sm font-medium text-[var(--sea-ink)]">
                  Request a new verification email
                </p>
                <form onSubmit={handleResend} className="space-y-3">
                  <div className="space-y-2">
                    <Label htmlFor="resend-email">Email address</Label>
                    <Input
                      id="resend-email"
                      type="email"
                      placeholder="name@example.com"
                      value={resendEmail}
                      onChange={(e) => setResendEmail(e.target.value)}
                      required
                      className="h-11 rounded-xl bg-white/50"
                    />
                  </div>
                  {resendMessage && (
                    <div
                      className={`rounded-xl p-3 text-sm font-medium ${
                        resendMessage.includes('resent')
                          ? 'bg-green-500/10 text-green-600'
                          : 'bg-red-500/10 text-red-600'
                      }`}
                    >
                      {resendMessage}
                    </div>
                  )}
                  <Button
                    type="submit"
                    className="w-full h-11 rounded-xl font-bold shadow-md"
                    disabled={resendLoading}
                  >
                    {resendLoading ? 'Sending...' : 'Resend Verification Email'}
                  </Button>
                </form>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
