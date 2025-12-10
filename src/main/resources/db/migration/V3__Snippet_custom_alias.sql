-- Migration: replace short_code with custom_alias (nullable, unique)
alter table snippets
    add column custom_alias varchar(100);

-- Drop unique on short_code if exists, then drop column
alter table snippets
    drop column if exists short_code;

-- Add unique index on custom_alias (nullable)
create unique index if not exists ux_snippets_custom_alias on snippets (custom_alias);

