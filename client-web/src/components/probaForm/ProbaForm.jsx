import { useState, useEffect } from 'react';
import styles from './ProbaForm.module.css';

const CATEGORII = ['6-8', '9-11', '12-15'];
const EMPTY = { nume: '', categorieVarsta: '6-8' };

export default function ProbaForm({ editTarget, onSubmit, onCancel, loading, isLoggedIn }) {
    const [form, setForm] = useState(EMPTY);

    useEffect(() => {
        console.log('editTarget:', editTarget);
        if (editTarget) {
            setForm({
                nume: editTarget.nume ?? '',
                categorieVarsta: editTarget.categorieVarsta });
        } else {
            setForm(EMPTY);
        }
    }, [editTarget]);

    const set = (field) => (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }));

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!form.nume.trim()) return;
        onSubmit(form);
    };

    const isEdit = Boolean(editTarget);

    return (
        <div className={styles.card}>
            <div className={styles.cardHeader}>
                <div className={styles.cardTitle}>
                    {isEdit ? `Editează proba #${editTarget.id}` : 'Adaugă probă nouă'}
                </div>
                {isEdit && <span className={styles.editBadge}>EDIT MODE</span>}
            </div>
            <form className={styles.form} onSubmit={handleSubmit}>
                <div className={styles.group}>
                    <label className={styles.label}>Nume probă</label>
                    <input
                        className={styles.input}
                        type="text"
                        placeholder="ex: Desen"
                        value={form.nume}
                        onChange={set('nume')}
                        required
                    />
                </div>
                <div className={styles.group}>
                    <label className={styles.label}>Categorie vârstă</label>
                    <div className={styles.pills}>
                        {CATEGORII.map((c) => (
                            <button
                                key={c}
                                type="button"
                                className={`${styles.pill} ${form.categorieVarsta === c ? styles.pillActive : ''}`}
                                onClick={() => setForm((p) => ({ ...p, categorieVarsta: c }))}
                            >
                                {c}
                            </button>
                        ))}
                    </div>
                </div>
                <div className={styles.actions}>
                    <button
                        type="submit"
                        className={styles.submit}
                        disabled={loading || !form.nume?.trim() || !isLoggedIn}
                        >
                        {loading ? '...' : isEdit ? 'Salvează' : 'Adaugă'}
                    </button>
                    {isEdit && (
                        <button
                            type="button"
                            className={styles.cancel}
                            onClick={onCancel}
                            disabled={!isLoggedIn}
                        >
                            Anulează
                        </button>
                    )}
                </div>
            </form>
        </div>
    );
}