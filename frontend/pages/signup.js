import { useEffect, useState } from 'react'
import apiFetch from '../utils/api.js'

export default function SignUp() {
  const [mounted, setMounted] = useState(false)
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    if (!mounted) return
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userId')
    if (token && userId) {
      window.location.href = '/dashboard'
    }
  }, [mounted])

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const res = await apiFetch('/login/signUp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password }),
      })
      const data = await res.json()
      if (res.ok && data?.id != null) {
        const loginRes = await apiFetch('/login/signIn', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, password }),
        })
        const loginData = await loginRes.json()
        if (loginRes.ok && loginData?.id != null) {
          const token = loginData.token || (loginRes.headers?.get('Authorization')?.replace('Bearer ', ''))
          if (token) {
            localStorage.setItem('token', token)
            localStorage.setItem('userId', loginData.id)
            localStorage.setItem('userEmail', email)
            window.location.href = '/dashboard'
            return
          } else {
            setError('Token not received')
          }
        } else {
          setError('Registration succeeded, but login failed. Please try logging in.')
        }
      } else {
        setError('Registration failed. Email may already be in use.')
      }
    } catch {
      setError('Registration failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  if (!mounted) {
    return (
      <div style={pageContainer}>
        <div style={card}>
          <div style={{ textAlign: 'center', padding: 40 }}>Loading...</div>
        </div>
      </div>
    )
  }

  return (
    <div style={pageContainer}>
      <div style={card}>
        <button onClick={() => window.location.href = '/'} style={backButton}>
          ← Back to Login
        </button>

        <div style={logoSection}>
          <div style={logo}>✓</div>
          <h1 style={title}>Create Account</h1>
          <p style={subtitle}>Start tracking your habits today</p>
        </div>

        <form onSubmit={handleSubmit} style={form}>
          <div style={field}>
            <label style={label}>Name</label>
            <input 
              placeholder="Enter your name" 
              value={name} 
              onChange={e => setName(e.target.value)} 
              style={input}
              required 
              minLength={3} 
              maxLength={20} 
            />
          </div>
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
              placeholder="Create a password" 
              type="password" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              style={input}
              required 
              minLength={6} 
            />
          </div>
          
          {error && <div style={errorBox}>{error}</div>}
          
          <button type="submit" disabled={loading} style={submitButton}>
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <div style={footer}>
          <p style={footerText}>Already have an account? <a href="/" style={link}>Sign in</a></p>
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
  background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
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

const backButton = {
  background: 'none',
  border: 'none',
  color: '#666',
  cursor: 'pointer',
  fontSize: 14,
  marginBottom: 20,
  padding: 0
}

const logoSection = {
  textAlign: 'center',
  marginBottom: 32
}

const logo = {
  width: 64,
  height: 64,
  borderRadius: '50%',
  background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
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
  background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
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
  color: '#f5576c',
  textDecoration: 'none',
  fontWeight: 600
}
