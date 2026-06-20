import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createJob } from '../api/jobs'

function PostJob() {
  const [form, setForm] = useState({
    title: '', category: '', description: '',
    locationLat: '', locationLng: '', locationName: ''
  })
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await createJob({
        ...form,
        locationLat: parseFloat(form.locationLat),
        locationLng: parseFloat(form.locationLng),
      })
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to post job')
    }
  }

  return (
    <div className="max-w-lg mx-auto px-6 py-12">
      <h1 className="font-display text-2xl text-charcoal mb-2">Post a job</h1>
      <p className="font-body text-sm text-charcoal/60 mb-6">
        Describe the work you need done. Candidates nearby will see it instantly.
      </p>

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          name="title"
          placeholder="Job title (e.g. Senior Plumber Needed)"
          value={form.title}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />
        <input
          name="category"
          placeholder="Category (e.g. Plumber, Driver, Cleaner)"
          value={form.category}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />
        <textarea
          name="description"
          placeholder="Describe the job"
          value={form.description}
          onChange={handleChange}
          required
          rows={4}
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />
        <input
          name="locationName"
          placeholder="Location name (e.g. New Delhi, India)"
          value={form.locationName}
          onChange={handleChange}
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />
        <div className="flex gap-3">
          <input
            name="locationLat"
            type="number"
            step="any"
            placeholder="Latitude"
            value={form.locationLat}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
          />
          <input
            name="locationLng"
            type="number"
            step="any"
            placeholder="Longitude"
            value={form.locationLng}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
          />
        </div>

        {error && <p className="text-red-600 text-sm font-body">{error}</p>}

        <button
          type="submit"
          className="w-full bg-clay text-white py-2.5 rounded-md font-body font-medium hover:bg-clay/90"
        >
          Post job
        </button>
      </form>
    </div>
  )
}

export default PostJob