import { useState, useEffect } from 'react'
import apiFetch from '../../utils/api.js'

export default function AdminLogin() {
  const [mounted, setMounted] = useState(false)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    if (!mounted) return
    const adminToken = localStorage.getItem('admin_token')
    if (adminToken) {
      window.location.href = '/admin'
    }
  }, [mounted])

  async function login(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    
    try {
      const apiBase = (typeof window !== 'undefined' && window.__API_BASE__) || (process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080')
      
      const resp = await fetch(`${apiBase}/login/signIn`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })
      
      const data = await resp.json()
      
      if (resp.ok && data.id != null) {
        const token = data.token || (resp.headers.get('Authorization')?.replace('Bearer ', ''))
        
        if (token) {
          if (data.role === 'ADMINISTRATOR') {
            localStorage.setItem('admin_token', token)
            localStorage.setItem('admin_userId', data.id)
            window.location.href = '/admin'
          } else {
            setError('Access denied. Administrator credentials required.')
          }
        } else {
          setError('Token not received')
        }
      } else {
        setError('Invalid credentials')
      }
    } catch {
      setError('Connection error')
    } finally {
      setLoading(false)
    }
  }

  if (!mounted) {
    return (
      <div style={{ ...pageContainer, display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
        <div style={{ color: '#fff' }}>Loading...</div>
      </div>
    )
  }

  return (
    <div style={pageContainer}>
      <div style={card}>
        <div style={logoSection}>
          <div style={logo}>🛡️</div>
          <h1 style={title}>Admin Login</h1>
          <p style={subtitle}>Enter administrator credentials</p>
        </div>

        <form onSubmit={login} style={form}>
          <div style={field}>
            <label style={label}>Email</label>
            <input 
              placeholder="Admin email" 
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
              placeholder="Password" 
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
          <a href="/" style={link}>← Back to User Login</a>
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
  background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)',
  padding: 20,
  fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
}

const card = {
  width: '100%',
  maxWidth: 400,
  background: '#16213e',
  borderRadius: 24,
  padding: 40,
  boxShadow: '0 20px 60px rgba(0,0,0,.4)'
}

const logoSection = {
  textAlign: 'center',
  marginBottom: 32
}

const logo = {
  width: 72,
  height: 72,
  borderRadius: '50%',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: '#fff',
  fontSize: 32,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  margin: '0 auto 16px'
}

const title = {
  margin: '0 0 8px 0',
  fontSize: 28,
  color: '#fff',
  fontWeight: 700
}

const subtitle = {
  margin: 0,
  color: '#888',
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
  color: '#ccc'
}

const input = {
  padding: '14px 16px',
  borderRadius: 12,
  border: '1px solid #0f3460',
  background: '#0f3460',
  color: '#fff',
  fontSize: 15,
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
  marginTop: 8
}

const footer = {
  marginTop: 28,
  textAlign: 'center'
}

const link = {
  color: '#888',
  textDecoration: 'none',
  fontSize: 14
}
