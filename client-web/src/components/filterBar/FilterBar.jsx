import { useState } from 'react';
import styles from './FilterBar.module.css';

const CATEGORII = ['', '6-8', '9-11', '12-15'];

export default function FilterBar({ onFilter, onReset, isFiltered }) {
    const [categorie, setCategorie] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (categorie) onFilter(categorie);
    };

    const handleReset = () => {
        setCategorie('');
        onReset();
    };

    return (
        <form className={styles.bar} onSubmit={handleSubmit}>
            <div className={styles.label}>Filtrare</div>
            <div className={styles.field}>
                <span className={styles.fieldLabel}>Categorie vârstă</span>
                <select
                    className={styles.select}
                    value={categorie}
                    onChange={(e) => setCategorie(e.target.value)}
                >
                    {CATEGORII.map((c) => (
                        <option key={c} value={c}>{c || '— selectează —'}</option>
                    ))}
                </select>
            </div>
            <button type="submit" className={styles.btn} disabled={!categorie}>
                Aplică
            </button>
            {isFiltered && (
                <button type="button" className={`${styles.btn} ${styles.btnReset}`} onClick={handleReset}>
                    ✕ Resetează
                </button>
            )}
        </form>
    );
}