import EmptyState from '../common/EmptyState'
import Table from '../common/Table'

const columns = [
  { key: 'title', header: 'Task' }, { key: 'projectName', header: 'Project' },
  { key: 'dueDate', header: 'Due date' }, { key: 'taskStatus', header: 'Status' },
]

function RecentTasks({ tasks }) { return tasks?.length ? <Table columns={columns} data={tasks} /> : <EmptyState title="No upcoming deadlines" description="There are no upcoming tasks to display." /> }
export default RecentTasks
