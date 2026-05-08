import { createFileRoute, Link } from '@tanstack/react-router'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import api from '@/lib/axios'
import { Shield, CircleCheckBig } from 'lucide-react'

export const Route = createFileRoute('/forgot-password')({
  component: ForgotPasswordPage,
})

function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [isSubmitted, setIsSubmitted] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)

    try {
      await api.post('auth/forgot-password', { email })
      setIsSubmitted(true)
    } catch (err: any) {
      setError(
        err.response?.data?.message || 'An error occurred. Please try again.',
      )
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-[calc(100vh-200px)] items-center justify-center px-4">
      <div className="island-shell rise-in w-full max-w-md rounded-[2rem] p-8 sm:p-10">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
            {isSubmitted ? (
              <CircleCheckBig size={28} className="text-green-600" />
            ) : (
              <Shield size={28} />
            )}
          </div>
          <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">
            {isSubmitted ? 'Check Your Email' : 'Forgot Password?'}
          </h1>
          <p className="mt-2 text-[var(--sea-ink-soft)]">
            {isSubmitted
              ? 'If an account exists with this email and is verified, you will receive a password reset link.'
              : 'Enter your email to receive a reset link'}
          </p>
        </div>

        {!isSubmitted ? (
          <form onSubmit={handleSubmit} className="space-y-6">
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
              {isLoading ? 'Sending...' : 'Send Reset Link'}
            </Button>

            <div className="text-center text-sm">
              <Link
                to="/login"
                className="font-bold text-[var(--lagoon-deep)] hover:underline"
              >
                Back to Login
              </Link>
            </div>
          </form>
        ) : (
          <div className="space-y-4">
            <p className="text-center text-sm text-[var(--sea-ink-soft)]">
              Didn't receive the email? Check your spam folder or try again.
            </p>
            <Button
              variant="outline"
              className="w-full h-11 rounded-xl font-bold"
              onClick={() => setIsSubmitted(false)}
            >
              Try a different email
            </Button>
            <div className="text-center text-sm">
              <Link
                to="/login"
                className="font-bold text-[var(--lagoon-deep)] hover:underline"
              >
                Back to Login
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
