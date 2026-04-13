-- BaseDatos.sql
-- PostgreSQL database script for the Devsu challenge.
--
-- The application runs customer-service and account-service with separate databases in
-- Docker Compose. Run the customer table section against the customer database and the
-- account/movement sections against the account database when using separated databases.

CREATE TABLE IF NOT EXISTS clientes (
  cliente_id VARCHAR(60) PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  genero VARCHAR(255),
  edad INTEGER,
  identificacion VARCHAR(255) NOT NULL UNIQUE,
  direccion VARCHAR(255) NOT NULL,
  telefono VARCHAR(255) NOT NULL,
  contrasena VARCHAR(255) NOT NULL,
  estado BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS cuentas (
  numero_cuenta VARCHAR(50) PRIMARY KEY,
  tipo_cuenta VARCHAR(50) NOT NULL,
  saldo_inicial NUMERIC(19, 2) NOT NULL,
  saldo_disponible NUMERIC(19, 2) NOT NULL,
  estado BOOLEAN NOT NULL,
  cliente_id VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS movimientos (
  movimiento_id VARCHAR(36) PRIMARY KEY,
  numero_cuenta VARCHAR(50) NOT NULL,
  fecha TIMESTAMP NOT NULL,
  tipo_movimiento VARCHAR(50) NOT NULL,
  valor NUMERIC(19, 2) NOT NULL,
  saldo NUMERIC(19, 2) NOT NULL,
  CONSTRAINT fk_movimientos_cuentas
    FOREIGN KEY (numero_cuenta)
    REFERENCES cuentas (numero_cuenta)
);

CREATE INDEX IF NOT EXISTS idx_cuentas_cliente_id ON cuentas (cliente_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_numero_cuenta_fecha
  ON movimientos (numero_cuenta, fecha);

INSERT INTO clientes (
  cliente_id,
  nombre,
  genero,
  edad,
  identificacion,
  direccion,
  telefono,
  contrasena,
  estado
) VALUES
  (
    'CLI-001',
    'Jose Lema',
    'Masculino',
    32,
    '0102030405',
    'Otavalo sn y principal',
    '098254785',
    '1234',
    TRUE
  ),
  (
    'CLI-002',
    'Marianela Montalvo',
    'Femenino',
    30,
    '0102030406',
    'Amazonas y NNUU',
    '097548965',
    '5678',
    TRUE
  ),
  (
    'CLI-003',
    'Juan Osorio',
    'Masculino',
    28,
    '0102030407',
    '13 junio y Equinoccial',
    '098874587',
    '1245',
    TRUE
  )
ON CONFLICT (cliente_id) DO NOTHING;

INSERT INTO cuentas (
  numero_cuenta,
  tipo_cuenta,
  saldo_inicial,
  saldo_disponible,
  estado,
  cliente_id
) VALUES
  ('478758', 'Ahorros', 2000.00, 1425.00, TRUE, 'CLI-001'),
  ('225487', 'Corriente', 100.00, 700.00, TRUE, 'CLI-002'),
  ('495878', 'Ahorros', 0.00, 150.00, TRUE, 'CLI-003'),
  ('496825', 'Ahorros', 540.00, 0.00, TRUE, 'CLI-002'),
  ('585545', 'Corriente', 1000.00, 1000.00, TRUE, 'CLI-001')
ON CONFLICT (numero_cuenta) DO NOTHING;

INSERT INTO movimientos (
  movimiento_id,
  numero_cuenta,
  fecha,
  tipo_movimiento,
  valor,
  saldo
) VALUES
  (
    '00000000-0000-0000-0000-000000000001',
    '478758',
    '2022-02-08 09:00:00',
    'Retiro',
    -575.00,
    1425.00
  ),
  (
    '00000000-0000-0000-0000-000000000002',
    '225487',
    '2022-02-10 09:00:00',
    'Deposito',
    600.00,
    700.00
  ),
  (
    '00000000-0000-0000-0000-000000000003',
    '495878',
    '2022-02-08 10:00:00',
    'Deposito',
    150.00,
    150.00
  ),
  (
    '00000000-0000-0000-0000-000000000004',
    '496825',
    '2022-02-08 11:00:00',
    'Retiro',
    -540.00,
    0.00
  )
ON CONFLICT (movimiento_id) DO NOTHING;
