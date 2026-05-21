import { useState, useEffect, useCallback } from 'react';
import { GetAllProbe, GetProbeByCategorie, AddProba, UpdateProba, DeleteProba } from './api/probeApi';
import { Login, Logout } from './api/probeApi';
import Header from './components/Header/Header';
import FilterBar from './components/FilterBar/FilterBar';
import ProbaForm from './components/ProbaForm/ProbaForm';
import ProbaTable from './components/ProbaTable/ProbaTable';
import Toast from './components/Toast/Toast';
import LoginPage from './components/login/LoginPage';
import styles from './App.module.css';

export default function App() {
  const [probe, setProbe] = useState([]);
  const [loading, setLoading] = useState(false);
  const [formLoading, setFormLoading] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [isFiltered, setIsFiltered] = useState(false);
  const [toast, setToast] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('jwt'));
  const [loginLoading, setLoginLoading] = useState(false);
  const [showLogin, setShowLogin] = useState(false);

  const handleLogin = async (username, password) => {
    setLoginLoading(true);
    try {
      await Login(username, password);
      setIsLoggedIn(true);
      setShowLogin(false);
      showToast('Autentificat cu succes!');
    } catch (e) {
      showToast(e.message, 'error');
    } finally {
      setLoginLoading(false);
    }
  };

  const handleLogout = () => {
    Logout();
    setIsLoggedIn(false);
    showToast('Deconectat.', 'info');
  };

  const showToast = useCallback((message, type = 'success') => {
    setToast({ message, type });
  }, []);

  const loadAll = useCallback(async () => {
    setLoading(true);
    try {
      const data = await GetAllProbe();
      setProbe(data);
      setIsFiltered(false);
    } catch (e) {
      showToast(e.message || 'Eroare la încărcare', 'error');
    } finally {
      setLoading(false);
    }
  }, [showToast]);

  useEffect(() => { loadAll(); }, [loadAll]);

  const handleFilter = async (categorie) => {
    setLoading(true);
    try {
      const data = await GetProbeByCategorie(categorie);
      setProbe(data);
      setIsFiltered(true);
      showToast(`${data.length} rezultate pentru "${categorie}"`, 'info');
    } catch (e) {
      showToast(e.message || 'Eroare la filtrare', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (formData) => {
    setFormLoading(true);
    try {
      if (editTarget) {
        await UpdateProba(editTarget.id, formData);
        showToast('Proba a fost actualizată!');
        setEditTarget(null);
      } else {
        const created = await AddProba(formData);
        showToast(`Proba #${created.id} a fost adăugată!`);
      }
      await loadAll();
    } catch (e) {
      showToast(e.message || 'Operație eșuată', 'error');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm(`Ștergi proba #${id}?`)) return;
    try {
      await DeleteProba(id);
      showToast(`Proba #${id} a fost ștearsă!`);
      await loadAll();
    } catch (e) {
      showToast(e.message || 'Eroare la ștergere', 'error');
    }
  };


  return (
      <div className={styles.app}>
        <Header
            isLoggedIn={isLoggedIn}
            // onLoginClick={() => setShowLogin(true)}
            onLoginClick={() => {
              console.log("SET LOGIN TRUE");
              setShowLogin(true);
            }}
            onLogout={handleLogout}
        />

        <main className={styles.main}>
          <div className={styles.sidebar}>
            <ProbaForm
                editTarget={editTarget}
                onSubmit={handleSubmit}
                onCancel={() => setEditTarget(null)}
                loading={formLoading}
                isLoggedIn={isLoggedIn}
            />
          </div>
          <div className={styles.content}>
            <FilterBar
                onFilter={handleFilter}
                onReset={loadAll}
                isFiltered={isFiltered}
            />
            <ProbaTable
                probe={probe}
                loading={loading}
                onEdit={(p) => {
                  setEditTarget(p);
                  setTimeout(() => console.log('editTarget dupa 100ms:', p), 100);
                }}
                onDelete={handleDelete}
                isFiltered={isFiltered}
                isLoggedIn={isLoggedIn}
            />
          </div>
        </main>
        {showLogin && (
            <LoginPage
                onLogin={handleLogin}
                loading={loginLoading}
                onClose={() => setShowLogin(false)}
            />
        )}

        {toast && (
            <Toast
                message={toast.message}
                type={toast.type}
                onClose={() => setToast(null)}
            />
        )}
      </div>
  );
}