import { useState } from 'react';
import styles from './Login.module.css';

export default function LoginPage({ onLogin, loading, onClose }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    return (
        <div className={styles.overlay}>
            <div className={styles.card}>
                <h2 className={styles.title}>Autentificare</h2>

                <input
                    className={styles.input}
                    placeholder="Username"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                />
                <input
                    className={styles.input}
                    placeholder="Parolă"
                    type="password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />

                <button className={styles.loginBtn} onClick={() => onLogin(username, password)} disabled={loading}>
                    {loading ? 'Se conectează...' : 'Login'}
                </button>

                {onClose && (
                    <button className={styles.cancelBtn} onClick={onClose}>Anulează</button>
                )}
            </div>
        </div>
    );
}