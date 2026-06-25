-- Extension pour les UUIDs générés
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table Utilisateurs
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'COMMERCIAL')),
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Table Produits
CREATE TABLE products (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          reference VARCHAR(50) UNIQUE NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          location VARCHAR(255),
                          min_stock_threshold INTEGER NOT NULL DEFAULT 0,
                          max_stock_threshold INTEGER NOT NULL DEFAULT 0,
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                          CONSTRAINT chk_thresholds CHECK (min_stock_threshold <= max_stock_threshold)
);

-- Table Mouvements de Stock
CREATE TABLE stock_movements (
                                 id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 product_id UUID NOT NULL REFERENCES products(id),
                                 user_id UUID NOT NULL REFERENCES users(id),
                                 movement_type VARCHAR(10) NOT NULL CHECK (movement_type IN ('ENTRY', 'EXIT')),
                                 quantity INTEGER NOT NULL,
                                 reason VARCHAR(255),
                                 client_id UUID, -- Optionnel, pour les sorties
                                 created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                                 CONSTRAINT chk_quantity CHECK (
                                     (movement_type = 'ENTRY' AND quantity > 0) OR
                                     (movement_type = 'EXIT' AND quantity < 0)
                                     )
);

-- Table Numéros de Série (le cœur de la traçabilité)
CREATE TABLE serial_numbers (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                product_id UUID NOT NULL REFERENCES products(id),
                                serial_number VARCHAR(100) UNIQUE NOT NULL,
                                status VARCHAR(20) NOT NULL DEFAULT 'IN_STOCK' CHECK (status IN ('IN_STOCK', 'SOLD', 'RETURNED', 'DEFECTIVE')),
                                entry_movement_id UUID NOT NULL REFERENCES stock_movements(id),
                                exit_movement_id UUID REFERENCES stock_movements(id),
                                client_id UUID, -- Client final (pour garantie)
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                                CONSTRAINT chk_exit_logic CHECK (
                                    (status = 'IN_STOCK' AND exit_movement_id IS NULL AND client_id IS NULL) OR
                                    (status IN ('SOLD', 'RETURNED', 'DEFECTIVE') AND exit_movement_id IS NOT NULL)
                                    )
);

-- Table Alertes de Réapprovisionnement
CREATE TABLE replenishment_alerts (
                                      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                      product_id UUID NOT NULL REFERENCES products(id),
                                      suggested_quantity INTEGER NOT NULL,
                                      status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'RESOLVED', 'DISMISSED')),
                                      resolved_at TIMESTAMP WITH TIME ZONE,
                                      created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Table Audit Log
CREATE TABLE audit_log (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           user_id UUID REFERENCES users(id),
                           action VARCHAR(50) NOT NULL,
                           entity_type VARCHAR(50) NOT NULL,
                           entity_id UUID NOT NULL,
                           old_value JSONB,
                           new_value JSONB,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

ALTER TABLE replenishment_alerts
    ADD COLUMN IF NOT EXISTS product_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS current_stock INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS min_threshold INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS max_threshold INTEGER NOT NULL DEFAULT 0;

-- Indexes pour performances
CREATE INDEX idx_serial_numbers_product_id ON serial_numbers(product_id);
CREATE INDEX idx_serial_numbers_status ON serial_numbers(status);
CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_created_at ON stock_movements(created_at);
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);