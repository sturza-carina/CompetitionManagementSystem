import styles from './ProbaTable.module.css';

const BADGE_COLORS = {
    '6-8': '#34d399',
    '9-11': '#c45c9e',
    '12-15': '#a78bfa'
};

export default function ProbaTable({ probe, loading, onEdit, onDelete, isFiltered, isLoggedIn }) {
    if (loading) {
        return (
            <div className={styles.state}>
                <div className={styles.spinner} />
                <span>Se încarcă...</span>
            </div>
        );
    }

    if (!probe.length) {
        return (
            <div className={styles.state}>
                <div className={styles.emptyIcon}>◌</div>
                <span>{isFiltered ? 'Nicio probă nu corespunde filtrului.' : 'Nu există probe înregistrate.'}</span>
            </div>
        );
    }

    return (
        <div className={styles.wrapper}>
            <div className={styles.tableHeader}>
        <span>{probe.length} {probe.length === 1 ? 'probă' : 'probe'}
            {isFiltered && <span className={styles.filterTag}> — filtrat</span>}
        </span>
            </div>
            <div className={styles.tableScroll}>
                <table className={styles.table}>
                    <thead>
                    <tr>
                        <th>Nume</th>
                        <th>Categorie</th>
                        <th className={styles.thActions}>Acțiuni</th>
                    </tr>
                    </thead>
                    <tbody>
                    {probe.map((p) => (
                        <tr key={p.id} className={styles.row}>
                            <td className={styles.tdNume}>{p.nume}</td>
                            <td>
                  <span className={styles.catBadge} style={{ '--cat-color': BADGE_COLORS[p.categorieVarsta] || '#9098b0' }}>
                    {p.categorieVarsta}
                  </span>
                            </td>
                            <td className={styles.tdActions}>
                                <button
                                    className={`${styles.action} ${styles.actionEdit}`}
                                    onClick={() => isLoggedIn ? onEdit(p) : null}
                                    disabled={!isLoggedIn}
                                >✎
                                </button>
                                <button
                                    className={`${styles.action} ${styles.actionDelete}`}
                                    onClick={() => isLoggedIn ? onDelete(p.id) : null}
                                    disabled={!isLoggedIn}
                                >✕
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}