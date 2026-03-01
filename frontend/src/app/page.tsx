import { Suspense } from 'react'
import { Dashboard } from '@/components/dashboard/dashboard'
import { LoadingSpinner } from '@/components/ui/loading-spinner'

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-900 dark:to-slate-800">
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-slate-900 dark:text-white mb-2">
            🚀 AutoRoot
          </h1>
          <p className="text-xl text-slate-600 dark:text-slate-300">
            AI-Powered API Observability Platform
          </p>
        </div>
        
        <Suspense fallback={<LoadingSpinner />}>
          <Dashboard />
        </Suspense>
      </main>
    </div>
  )
}