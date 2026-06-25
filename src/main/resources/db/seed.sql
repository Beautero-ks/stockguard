-- Données initiales pour l'environnement de développement
-- Insérer un admin par défaut (mot de passe : admin123)
-- Le hash BCrypt est généré à part, voici pour "admin123"
-- À générer avec : spring encodepassword admin123
INSERT INTO users (id, email, password_hash, full_name, role, enabled)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           'admin@stockguard.dev',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           'Admin Système',
           'ADMIN',
           true
       ) ON CONFLICT (email) DO NOTHING;