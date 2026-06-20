import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { registerUser } from '../api/auth'
import { useAuth } from '../context/AuthContext'

function Register() {
  const [form, setForm] = useState({
    email: '', password: '', fullName: '', phone: '', role: 'CANDIDATE'
  })
  const [error, setError] = useState('')
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const response = await registerUser(form)
      login(response.data)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || 'Registration failed')
    }
  }

  return (
    <div className="max-w-sm mx-auto px-6 py-16">
      <h1 className="font-display text-2xl text-charcoal mb-6">Create an account</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          name="fullName"
          placeholder="Full name"
          value={form.fullName}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />
        <input
          name="email"
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />
        <input
          name="phone"
          placeholder="Phone"
          value={form.phone}
          onChange={handleChange}
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />

        <div className="flex gap-3">
          <label className="flex-1 flex items-center gap-2 border border-sand rounded-md px-4 py-2 cursor-pointer has-[:checked]:border-clay has-[:checked]:bg-clay/5">
            <input
              type="radio"
              name="role"
              value="CANDIDATE"
              checked={form.role === 'CANDIDATE'}
              onChange={handleChange}
            />
            <span className="font-body text-sm">Find work</span>
          </label>
          <label className="flex-1 flex items-center gap-2 border border-sand rounded-md px-4 py-2 cursor-pointer has-[:checked]:border-clay has-[:checked]:bg-clay/5">
            <input
              type="radio"
              name="role"
              value="EMPLOYER"
              checked={form.role === 'EMPLOYER'}
              onChange={handleChange}
            />
            <span className="font-body text-sm">Hire someone</span>
          </label>
        </div>

        {error && <p className="text-red-600 text-sm font-body">{error}</p>}

        <button
          type="submit"
          className="w-full bg-clay text-white py-2.5 rounded-md font-body font-medium hover:bg-clay/90"
        >
          Create account
        </button>
      </form>

      <p className="mt-4 text-sm font-body text-charcoal/60">
        Already have an account? <Link to="/login" className="text-clay">Log in</Link>
      </p>
    </div>
  )
}

export default Register