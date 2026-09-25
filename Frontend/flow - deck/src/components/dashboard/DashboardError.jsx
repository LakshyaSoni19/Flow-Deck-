import Button from '../common/Button'
import EmptyState from '../common/EmptyState'

function DashboardError({ message, onRetry }) { return <EmptyState title="Dashboard unavailable" description={message} action={<Button type="button" onClick={onRetry}>Try again</Button>} /> }
export default DashboardError
