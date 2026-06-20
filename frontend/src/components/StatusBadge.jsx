function StatusBadge({ status }) {
  const isOpen = status === 'OPEN'
  return (
    <span className={`inline-block px-3 py-1 rounded-full text-xs font-body font-medium ${
      isOpen ? 'bg-sage/20 text-sage' : 'bg-charcoal/10 text-charcoal/60'
    }`}>
      {isOpen ? 'Open' : 'Closed'}
    </span>
  )
}

export default StatusBadge