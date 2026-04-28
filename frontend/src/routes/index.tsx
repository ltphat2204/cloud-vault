import { createFileRoute, Link } from '@tanstack/react-router'
import { Button } from '@/components/ui/button'
import { Shield, Cloud, Lock, Zap } from 'lucide-react'

export const Route = createFileRoute('/')({
  component: LandingPage,
})

function LandingPage() {
  return (
    <main className="page-wrap px-4 pb-20 pt-16">
      {/* Hero Section */}
      <section className="island-shell rise-in relative overflow-hidden rounded-[2.5rem] px-6 py-16 text-center sm:px-12 sm:py-24">
        <div className="pointer-events-none absolute -left-20 -top-24 h-64 w-64 rounded-full bg-[radial-gradient(circle,rgba(79,184,178,0.4),transparent_70%)]" />
        <div className="pointer-events-none absolute -bottom-24 -right-20 h-64 w-64 rounded-full bg-[radial-gradient(circle,rgba(47,106,74,0.25),transparent_70%)]" />

        <div className="relative z-10 mx-auto max-w-3xl">
          <p className="island-kicker mb-4">Secure Cloud Storage</p>
          <h1 className="display-title mb-6 text-5xl leading-[1.1] font-extrabold tracking-tight text-[var(--sea-ink)] sm:text-7xl">
            CloudVault: Your Data,{' '}
            <span className="text-[var(--lagoon-deep)]">Fortified.</span>
          </h1>
          <p className="mb-10 text-lg leading-relaxed text-[var(--sea-ink-soft)] sm:text-xl">
            Experience the next generation of file storage. Encrypted,
            distributed, and blazingly fast. Keep your digital life safe in the
            vault.
          </p>

          <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
            <Button
              asChild
              size="lg"
              className="h-12 rounded-full px-8 text-base font-bold shadow-lg transition-all hover:scale-105 active:scale-95"
            >
              <Link transition-all duration-300 ease-in-out to="/register">
                Get Started Free
              </Link>
            </Button>
            <Button
              asChild
              variant="outline"
              size="lg"
              className="h-12 rounded-full border-2 px-8 text-base font-bold transition-all hover:bg-[var(--lagoon)]/10 hover:scale-105 active:scale-95"
            >
              <Link to="/login">Sign In</Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section className="mt-16 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {[
          {
            icon: Shield,
            title: 'Triple-Link Security',
            desc: 'Our proprietary encryption ensures your data remains yours alone.',
          },
          {
            icon: Zap,
            title: 'Lightning Speed',
            desc: 'Global CDN distribution for instant access anywhere on the planet.',
          },
          {
            icon: Lock,
            title: 'Zero Knowledge',
            desc: "We can't see your data. Even if we wanted to. It's truly private.",
          },
          {
            icon: Cloud,
            title: 'Seamless Sync',
            desc: 'Access your files from any device, anytime, with instant syncing.',
          },
        ].map((feature, index) => (
          <article
            key={feature.title}
            className="island-shell feature-card rise-in flex flex-col items-center rounded-3xl p-8 text-center transition-all hover:shadow-xl"
            style={{ animationDelay: `${index * 100 + 150}ms` }}
          >
            <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
              <feature.icon size={28} />
            </div>
            <h2 className="mb-3 text-lg font-bold text-[var(--sea-ink)]">
              {feature.title}
            </h2>
            <p className="m-0 text-sm leading-relaxed text-[var(--sea-ink-soft)]">
              {feature.desc}
            </p>
          </article>
        ))}
      </section>

      {/* Footer Quote */}
      <section
        className="rise-in mt-20 text-center"
        style={{ animationDelay: '600ms' }}
      >
        <p className="text-sm font-medium tracking-widest text-[var(--sea-ink-soft)] uppercase">
          Trusted by over 10,000+ privacy enthusiasts
        </p>
      </section>
    </main>
  )
}
