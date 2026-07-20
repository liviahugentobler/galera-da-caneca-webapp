-- ============================================================
-- Migração Etapa 9 — executar apenas se o banco Galera_da_Caneca
-- já existir (criado nas etapas anteriores). Quem for recriar o
-- banco do zero pode usar direto o GaleraDaCaneca_database.sql
-- atualizado (já contém esta coluna).
-- ============================================================

ALTER TABLE vendas ADD COLUMN quantidade INT DEFAULT 1;
