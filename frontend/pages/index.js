import { useState, useEffect } from 'react'

export default function Home() {
  const [mounted, setMounted] = useState(false)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  const apiBase = mounted 
    ? (window.__API_BASE__ || process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080')
    : 'http://localhost:8080'

  async function login(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const resp = await fetch(`${apiBase}/login/signIn`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })
      const data = await resp.json()
      if (resp.ok && data.id != null) {
        const token = data.token || (resp.headers.get('Authorization')?.replace('Bearer ', ''))
        if (token) {
          localStorage.setItem('token', token)
          localStorage.setItem('userId', data.id)
          localStorage.setItem('userEmail', email)
          window.location.href = '/dashboard'
        } else {
          setError('Token not received')
        }
      } else {
        setError('Invalid email or password')
      }
    } catch {
      setError('Connection error. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={pageContainer}>
      <div style={card}>
        <div style={logoSection}>
          <div style={logo}>✓</div>
          <h1 style={title}>Habit Tracker</h1>
          <p style={subtitle}>Track your daily habits and achieve your goals</p>
        </div>

        <form onSubmit={login} style={form}>
          <div style={field}>
            <label style={label}>Email</label>
            <input 
              placeholder="Enter your email" 
              type="email"
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              style={input}
              required 
            />
          </div>
          <div style={field}>
            <label style={label}>Password</label>
            <input 
              placeholder="Enter your password" 
              type="password" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              style={input}
              required 
            />
          </div>
          
          {error && <div style={errorBox}>{error}</div>}
          
          <button type="submit" disabled={loading} style={submitButton}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div style={footer}>
          <p style={footerText}>Don't have an account? <a href="/signup" style={link}>Create one</a></p>
          <p style={{ marginTop: 12 }}>
            <a href="/reset" style={forgotLink}>Forgot password?</a>
          </p>
        </div>
      </div>
    </div>
  )
}

const pageContainer = {
  minHeight: '100vh',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  padding: 20,
  fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
}

const card = {
  width: '100%',
  maxWidth: 400,
  background: '#fff',
  borderRadius: 24,
  padding: 40,
  boxShadow: '0 20px 60px rgba(0,0,0,.2)'
}

const logoSection = {
  textAlign: 'center',
  marginBottom: 32
}

const logo = {
  width: 64,
  height: 64,
  borderRadius: '50%',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: '#fff',
  fontSize: 28,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  margin: '0 auto 16px',
  fontWeight: 'bold'
}

const title = {
  margin: '0 0 8px 0',
  fontSize: 28,
  color: '#1a1a2e',
  fontWeight: 700
}

const subtitle = {
  margin: 0,
  color: '#666',
  fontSize: 14
}

const form = {
  display: 'flex',
  flexDirection: 'column',
  gap: 20
}

const field = {
  display: 'flex',
  flexDirection: 'column',
  gap: 6
}

const label = {
  fontSize: 14,
  fontWeight: 500,
  color: '#333'
}

const input = {
  padding: '14px 16px',
  borderRadius: 12,
  border: '1px solid #ddd',
  fontSize: 15,
  transition: 'border-color 0.2s',
  outline: 'none'
}

const errorBox = {
  padding: '12px 16px',
  background: '#ffebee',
  color: '#c62828',
  borderRadius: 10,
  fontSize: 14,
  textAlign: 'center'
}

const submitButton = {
  padding: '16px',
  borderRadius: 12,
  border: 'none',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: '#fff',
  fontSize: 16,
  fontWeight: 600,
  cursor: 'pointer',
  marginTop: 8,
  transition: 'transform 0.1s, opacity 0.2s'
}

const footer = {
  marginTop: 28,
  textAlign: 'center'
}

const footerText = {
  margin: 0,
  color: '#666',
  fontSize: 14
}

const link = {
  color: '#667eea',
  textDecoration: 'none',
  fontWeight: 600
}

const forgotLink = {
  color: '#888',
  textDecoration: 'none',
  fontSize: 13
}
