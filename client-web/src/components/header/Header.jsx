import styles from './Header.module.css';

export default function Header({ isLoggedIn, onLoginClick, onLogout }) {
    return (
        <header className={styles.header}>

            <div className={styles.logo}>
                <span className={styles.logoIcon}>⬡</span>
                <div>
                    <div className={styles.logoTitle}>PROBE</div>
                    <div className={styles.logoSub}>Manager Concurs</div>
                </div>
            </div>

            <div className={styles.right}>
                <div className={styles.tag}>REST Client v1.0</div>

                <div className={styles.actions}>
                    {!isLoggedIn ? (
                        <button
                            className={styles.loginBtn}
                            onClick={() => {
                                onLoginClick();
                            }}
                        >
                            Login
                        </button>
                    ) : (
                        <>
                            <button
                                className={styles.logoutBtn}
                                onClick={onLogout}
                            >
                                Logout
                            </button>
                        </>
                    )}
                </div>
            </div>

        </header>
    );
}