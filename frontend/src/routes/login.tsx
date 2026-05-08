import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useAuth } from '@/hooks/use-auth'
import api from '@/lib/axios'
import { Shield } from 'lucide-react'

export const Route = createFileRoute('/login')({
  component: LoginPage,
})

function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)

    try {
      const response = await api.post('auth/login', { email, password })
      const { accessToken, user } = response.data.data
      login(accessToken, user)
      navigate({ to: '/dashboard' })
    } catch (err: any) {
      setError(
        err.response?.data?.message ||
          'Login failed. Please check your credentials.',
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
            <Shield size={28} />
          </div>
          <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">
            Welcome Back
          </h1>
          <p className="mt-2 text-[var(--sea-ink-soft)]">
            Enter your credentials to access your vault
          </p>
        </div>

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

          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <Label htmlFor="password">Password</Label>
              <Link
                to="/forgot-password"
                className="text-xs font-medium text-[var(--lagoon-deep)] hover:underline"
              >
                Forgot password?
              </Link>
            </div>
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
            {isLoading ? 'Signing in...' : 'Sign In'}
          </Button>
        </form>

        <div className="mt-8 text-center text-sm">
          <span className="text-[var(--sea-ink-soft)]">
            Don't have an account?{' '}
          </span>
          <Link
            to="/register"
            className="font-bold text-[var(--lagoon-deep)] hover:underline"
          >
            Create an account
          </Link>
        </div>
      </div>
    </div>
  )
}
