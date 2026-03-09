import React, { useEffect, useState } from 'react'

export default function HabitForm({ initialData, onSubmit, onCancel }) {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [frequency, setFrequency] = useState('daily')

  useEffect(() => {
    if (initialData) {
      setName(initialData.name || '')
      setDescription(initialData.description || '')
      setFrequency((initialData.frequency || 'daily').toLowerCase())
    }
  }, [initialData])

  function submit(e) {
    e.preventDefault()
    onSubmit({ name, description, frequency })
  }

  return (
    <form onSubmit={submit} style={formStyle}>
      <div style={fieldStyle}>
        <label style={labelStyle}>Habit Name</label>
        <input 
          value={name} 
          onChange={e => setName(e.target.value)} 
          style={inputStyle}
          required 
          minLength={3} 
          maxLength={20}
          placeholder="e.g., Exercise, Read, Meditate"
        />
      </div>
      <div style={fieldStyle}>
        <label style={labelStyle}>Description (optional)</label>
        <textarea 
          value={description} 
          onChange={e => setDescription(e.target.value)} 
          style={textareaStyle}
          maxLength={200}
          placeholder="Describe your habit..."
          rows={3}
        />
      </div>
      <div style={fieldStyle}>
        <label style={labelStyle}>Frequency</label>
        <div style={radioGroup}>
          <label style={radioLabel}>
            <input 
              type="radio" 
              name="frequency" 
              value="daily" 
              checked={frequency === 'daily'}
              onChange={e => setFrequency(e.target.value)}
              style={radioInput}
            />
            <span style={radioSpan}>📅 Daily</span>
          </label>
          <label style={radioLabel}>
            <input 
              type="radio" 
              name="frequency" 
              value="weekly" 
              checked={frequency === 'weekly'}
              onChange={e => setFrequency(e.target.value)}
              style={radioInput}
            />
            <span style={radioSpan}>📆 Weekly</span>
          </label>
        </div>
      </div>
      <div style={actionsStyle}>
        <button type="button" onClick={onCancel} style={cancelStyle}>Cancel</button>
        <button type="submit" style={submitStyle}>💾 Save Habit</button>
      </div>
    </form>
  )
}

const formStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: 20,
  minWidth: 340
}

const fieldStyle = { 
  display: 'flex', 
  flexDirection: 'column',
  gap: 8
}

const labelStyle = {
  fontSize: 14,
  fontWeight: 500,
  color: '#333'
}

const inputStyle = {
  padding: '14px 16px',
  borderRadius: 10,
  border: '1px solid #ddd',
  fontSize: 15,
  outline: 'none',
  transition: 'border-color 0.2s'
}

const textareaStyle = {
  padding: '14px 16px',
  borderRadius: 10,
  border: '1px solid #ddd',
  fontSize: 15,
  outline: 'none',
  resize: 'vertical',
  fontFamily: 'inherit'
}

const radioGroup = {
  display: 'flex',
  gap: 16
}

const radioLabel = {
  display: 'flex',
  alignItems: 'center',
  gap: 8,
  cursor: 'pointer'
}

const radioInput = {
  width: 18,
  height: 18,
  accentColor: '#667eea'
}

const radioSpan = {
  fontSize: 14,
  fontWeight: 500
}

const actionsStyle = { 
  display: 'flex', 
  justifyContent: 'flex-end', 
  gap: 12, 
  marginTop: 8 
}

const cancelStyle = { 
  padding: '12px 20px', 
  borderRadius: 10, 
  border: '1px solid #ddd', 
  background: '#fff', 
  cursor: 'pointer',
  fontWeight: 500,
  color: '#666'
}

const submitStyle = { 
  padding: '12px 24px', 
  borderRadius: 10, 
  border: 'none', 
  background: 'linear-gradient(135deg, #667eea, #764ba2)', 
  color: '#fff', 
  cursor: 'pointer',
  fontWeight: 600
}
