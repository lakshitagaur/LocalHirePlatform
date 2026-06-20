import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getJobById } from '../api/jobs'
import { applyToJob } from '../api/applications'
import { useAuth } from '../context/AuthContext'
import StatusBadge from '../components/StatusBadge'

function JobDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()

  const [job, setJob] = useState(null)
  const [loading, setLoading] = useState(true)
  const [coverNote, setCoverNote] = useState('')
  const [applying, setApplying] = useState(false)
  const [applySuccess, setApplySuccess] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchJob()
  }, [id])

  const fetchJob = async () => {
    setLoading(true)
    try {
      const response = await getJobById(id)
      setJob(response.data)
    } catch (err) {
      setError('Job not found')
    } finally {
      setLoading(false)
    }
  }

  const handleApply = async (e) => {
    e.preventDefault()
    setApplying(true)
    setError('')
    try {
      await applyToJob({ jobId: id, coverNote })
      setApplySuccess(true)
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to apply')
    } finally {
      setApplying(false)
    }
  }

  if (loading) {
    return <div className="max-w-3xl mx-auto px-6 py-10 font-body text-charcoal/50">Loading...</div>
  }

  if (!job) {
    return <div className="max-w-3xl mx-auto px-6 py-10 font-body text-charcoal/50">Job not found.</div>
  }

  return (
    <div className="max-w-3xl mx-auto px-6 py-10">
      <button
        onClick={() => navigate('/')}
        className="font-body text-sm text-charcoal/50 hover:text-charcoal mb-6"
      >
        ← Back to all jobs
      </button>

      <div className="bg-white border border-sand rounded-lg p-8">
        <div className="flex items-start justify-between mb-4">
          <span className="inline-block bg-clay/10 text-clay text-xs font-body font-semibold px-2.5 py-1 rounded-full uppercase tracking-wide">
            {job.category}
          </span>
          <StatusBadge status={job.status} />
        </div>

        <h1 className="font-display text-3xl text-charcoal mb-2">{job.title}</h1>
        <p className="font-body text-sm text-charcoal/60 mb-6">{job.locationName}</p>

        <p className="font-body text-charcoal/80 whitespace-pre-line mb-6">{job.description}</p>

        <div className="pt-4 border-t border-sand">
          <p className="font-body text-sm text-charcoal/50">Posted by {job.employerName}</p>
        </div>
      </div>

      {/* Apply section — only for candidates, only if job is open */}
      {user?.role === 'CANDIDATE' && job.status === 'OPEN' && (
        <div className="mt-6 bg-white border border-sand rounded-lg p-8">
          {applySuccess ? (
            <p className="font-body text-sage font-medium">
              ✓ Application submitted! The employer will review it soon.
            </p>
          ) : (
            <form onSubmit={handleApply} className="space-y-4">
              <h2 className="font-display text-xl text-charcoal">Apply for this job</h2>
              <textarea
                placeholder="Write a short note about why you're a good fit (optional)"
                value={coverNote}
                onChange={(e) => setCoverNote(e.target.value)}
                rows={4}
                className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
              />
              {error && <p className="text-red-600 text-sm font-body">{error}</p>}
              <button
                type="submit"
                disabled={applying}
                className="bg-clay text-white px-6 py-2.5 rounded-md font-body font-medium hover:bg-clay/90 disabled:opacity-50"
              >
                {applying ? 'Submitting...' : 'Submit application'}
              </button>
            </form>
          )}
        </div>
      )}

      {!user && job.status === 'OPEN' && (
        <div className="mt-6 bg-sand/30 border border-sand rounded-lg p-6 text-center">
          <p className="font-body text-sm text-charcoal/60">
            <a href="/login" className="text-clay font-medium">Log in</a> as a candidate to apply for this job.
          </p>
        </div>
      )}
    </div>
  )
}

export default JobDetail