-- 2026-11-15 : acceptation explicite des CGU (version et date), preuve du consentement du joueur.
ALTER TABLE public.user_entry
    ADD COLUMN terms_version varchar(32),
    ADD COLUMN terms_accepted_at timestamp without time zone;

COMMENT ON COLUMN public.user_entry.terms_version IS 'Version des CGU acceptée ; NULL = jamais acceptées explicitement';
