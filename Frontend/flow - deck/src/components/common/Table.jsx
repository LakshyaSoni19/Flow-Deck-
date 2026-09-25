import EmptyState from './EmptyState'
import styles from './ui.module.css'

function Table({ columns, data, emptyMessage = 'No records available.' }) {
  if (!data.length) return <EmptyState title="No records" description={emptyMessage} />

  return (
    <div className={styles.tableWrapper}>
      <table className={styles.table}>
        <thead><tr>{columns.map((column) => <th key={column.key}>{column.header}</th>)}</tr></thead>
        <tbody>{data.map((row, index) => <tr key={row.id ?? index}>{columns.map((column) => <td key={column.key}>{column.render ? column.render(row) : row[column.key]}</td>)}</tr>)}</tbody>
      </table>
    </div>
  )
}

export default Table
