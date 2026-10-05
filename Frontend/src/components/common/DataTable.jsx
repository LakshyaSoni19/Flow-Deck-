import EmptyState from './EmptyState'
import Pagination from './Pagination'
import SearchBar from './SearchBar'
import Table from './Table'
import styles from './architecture.module.css'

function DataTable({ columns, data = [], search, pagination, emptyMessage }) {
  return <section className={styles.dataTable}>{search && <SearchBar {...search} />}{data.length ? <Table columns={columns} data={data} emptyMessage={emptyMessage} /> : <EmptyState title="No records" description={emptyMessage} />}{pagination && <Pagination {...pagination} />}</section>
}
export default DataTable
