import { createFileRoute, Link } from '@tanstack/react-router'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import api from '@/lib/axios'
import { Shield, CircleCheckBig, CircleX } from 'lucide-react'
import { z } from 'zod'

const searchSchema = z.object({
  token: z.string().min(1).optional(),
})

export const Route = createFileRoute('/reset-password')({
  validateSearch: (search) => searchSchema.parse(search),
  component: ResetPasswordPage,
})

function ResetPasswordPage() {
  const { token } = Route.useSearch()
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [status, setStatus] = useState<'form' | 'loading' | 'success' | 'error'>(
    !token ? 'error' : 'form',
  )
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (newPassword !== confirmPassword) {
      setError('Passwords do not match.')
      return
    }

    setIsLoading(true)
    setStatus('loading')
    try {
      await api.post('auth/reset-password', { token, newPassword })
      setStatus('success')
    } catch (err: any) {
      setError(
        err.response?.data?.message || 'Password reset failed. Please try again.',
      )
      setStatus('error')
    } finally {
      setIsLoading(false)
    }
  }

  const icon =
    status === 'loading' || status === 'form' ? (
      <Shield size={28} />
    ) : status === 'success' ? (
      <CircleCheckBig size={28} className="text-green-600" />
    ) : (
      <CircleX size={28} className="text-red-500" />
    )

  const title =
    !token
      ? 'Invalid Reset Link'
      : status === 'loading'
        ? 'Resetting Password...'
        : status === 'success'
          ? 'Password Reset Successful!'
          : status === 'error' && error
            ? 'Reset Failed'
            : 'Reset Your Password'

  const subtitle =
    !token
      ? 'Invalid reset link. No token provided.'
      : status === 'loading'
        ? 'Please wait...'
        : status === 'success'
          ? 'Your password has been reset. You can now log in with your new password.'
          : status === 'error' && error
            ? error
            : 'Enter your new password below'

  return (
    <div className="flex min-h-[calc(100vh-200px)] items-center justify-center px-4">
      <div className="island-shell rise-in w-full max-w-md rounded-[2rem] p-8 sm:p-10">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
            {icon}
          </div>
          <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">
            {title}
          </h1>
          <p className="mt-2 text-[var(--sea-ink-soft)]">{subtitle}</p>
        </div>

        {(!token || status === 'error') && (
          <div className="space-y-4">
            {status === 'error' && (
              <Button
                asChild
                variant="outline"
                className="w-full h-11 rounded-xl font-bold"
              >
                <Link to="/forgot-password">Request a new reset link</Link>
              </Button>
            )}
            <Button asChild className="w-full h-11 rounded-xl font-bold shadow-md">
              <Link to="/login">Back to Login</Link>
            </Button>
          </div>
        )}

        {token && status === 'success' && (
          <Button asChild className="w-full h-11 rounded-xl font-bold shadow-md">
            <Link to="/login">Sign In</Link>
          </Button>
        )}

        {token && (status === 'form' || status === 'loading') && (
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="newPassword">New Password</Label>
              <Input
                id="newPassword"
                type="password"
                placeholder="••••••••"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
                minLength={8}
                autoComplete="new-password"
                className="h-11 rounded-xl bg-white/50"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirm Password</Label>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                minLength={8}
                autoComplete="new-password"
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
              {isLoading ? 'Resetting...' : 'Reset Password'}
            </Button>

            <div className="text-center text-sm">
              <Link
                to="/login"
                className="font-medium text-[var(--lagoon-deep)] hover:underline"
              >
                Back to Login
              </Link>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}
