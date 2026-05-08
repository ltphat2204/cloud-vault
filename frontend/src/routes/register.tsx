import { createFileRoute, Link } from '@tanstack/react-router'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import api from '@/lib/axios'
import { Shield, CircleCheckBig } from 'lucide-react'

export const Route = createFileRoute('/register')({
  component: RegisterPage,
})

function RegisterPage() {
  const [email, setEmail] = useState('')
  const [fullName, setFullName] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isRegistered, setIsRegistered] = useState(false)
  const [resendLoading, setResendLoading] = useState(false)
  const [resendMessage, setResendMessage] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)

    try {
      await api.post('auth/register', { email, name: fullName, password })
      setIsRegistered(true)
    } catch (err: any) {
      setError(
        err.response?.data?.message || 'Registration failed. Please try again.',
      )
    } finally {
      setIsLoading(false)
    }
  }

  const handleResend = async (e: React.FormEvent) => {
    e.preventDefault()
    setResendLoading(true)
    setResendMessage(null)
    try {
      await api.post('auth/resend-verification', null, {
        params: { email },
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

  if (isRegistered) {
    return (
      <div className="flex min-h-[calc(100vh-200px)] items-center justify-center px-4">
        <div className="island-shell rise-in w-full max-w-md rounded-[2rem] p-8 sm:p-10">
          <div className="mb-8 text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
              <CircleCheckBig size={28} className="text-green-600" />
            </div>
            <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">
              Check Your Email
            </h1>
            <p className="mt-2 text-[var(--sea-ink-soft)]">
              We've sent a verification link to <strong>{email}</strong>. Please verify your account before signing in.
            </p>
          </div>

          <div className="space-y-4">
            <div className="rounded-xl border border-[var(--lagoon)]/20 bg-[var(--lagoon)]/5 p-4">
              <p className="mb-3 text-sm font-medium text-[var(--sea-ink)]">
                Didn't receive the email?
              </p>
              <form onSubmit={handleResend} className="space-y-3">
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
                  variant="outline"
                  className="w-full h-11 rounded-xl font-bold"
                  disabled={resendLoading}
                >
                  {resendLoading ? 'Sending...' : 'Resend Verification Email'}
                </Button>
              </form>
            </div>

            <Button asChild className="w-full h-11 rounded-xl font-bold shadow-md">
              <Link to="/login">Back to Login</Link>
            </Button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="flex min-h-[calc(100vh-200px)] items-center justify-center px-4">
      <div className="island-shell rise-in w-full max-w-md rounded-[2rem] p-8 sm:p-10">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
            <Shield size={28} />
          </div>
          <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">
            Join CloudVault
          </h1>
          <p className="mt-2 text-[var(--sea-ink-soft)]">
            Create your account to start securing your files
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="space-y-2">
            <Label htmlFor="fullName">Full Name</Label>
            <Input
              id="fullName"
              placeholder="John Doe"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
              className="h-11 rounded-xl bg-white/50"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="email">Email address</Label>
            <Input
              id="email"
              type="email"
              placeholder="name@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="h-11 rounded-xl bg-white/50"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="h-11 rounded-xl bg-white/50"
            />
          </div>

          {error && (
            <div className="rounded-xl bg-red-500/10 p-3 text-sm font-medium text-red-600">
              {error}
            </div>
          )}

          <Button
            type="submit"
            className="w-full h-11 rounded-xl font-bold shadow-md"
            disabled={isLoading}
          >
            {isLoading ? 'Creating account...' : 'Create Account'}
          </Button>
        </form>

        <div className="mt-8 text-center text-sm">
          <span className="text-[var(--sea-ink-soft)]">
            Already have an account?{' '}
          </span>
          <Link
            to="/login"
            className="font-bold text-[var(--lagoon-deep)] hover:underline"
          >
            Sign in instead
          </Link>
        </div>
      </div>
    </div>
  )
}
