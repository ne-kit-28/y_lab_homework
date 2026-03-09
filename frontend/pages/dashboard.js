import { useEffect, useState } from 'react'
import HabitsManager from '../components/HabitsManager'

export default function Dashboard() {
  const [mounted, setMounted] = useState(false)
  useEffect(() => {
    setMounted(true)
  }, [])

  if (!mounted) return null

  return (
    <div style={pageContainer}>
      <style>{`
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background: #f5f7fa; }
      `}</style>
      <div style={content}>
        <h1 style={title}>Habit Tracker</h1>
        <HabitsManager />
      </div>
    </div>
  )
}

const pageContainer = {
  minHeight: '100vh',
  background: '#f5f7fa'
}

const content = {
  maxWidth: 1200,
  margin: '0 auto',
  padding: '20px'
}

const title = {
  display: 'none'
}
