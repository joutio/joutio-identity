-- Date de naissance privée portée par UserAggregate (design/specs/screens/editer-profil.md § 5).
-- Jamais exposée hors du joueur concerné ; effacée avec le compte (anonymisation de user_entry).
ALTER TABLE public.user_entry
    ADD COLUMN birth_date date;

COMMENT ON COLUMN public.user_entry.birth_date IS 'Date de naissance (privée), 15 ans minimum ; NULL = non renseignée';
