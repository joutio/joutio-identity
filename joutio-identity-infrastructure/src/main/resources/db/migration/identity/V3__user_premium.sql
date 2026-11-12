-- Droit Premium porté par UserAggregate (premium/specs/entitlements.md § 1.1).
ALTER TABLE public.user_entry
    ADD COLUMN premium_source character varying(20),
    ADD COLUMN premium_until timestamp without time zone,
    ADD COLUMN premium_granted_by character varying(255),
    ADD COLUMN premium_granted_at timestamp without time zone;

COMMENT ON COLUMN public.user_entry.premium_source IS 'Origine du droit Premium (MANUAL, APP_STORE, PLAY, WEB) ; NULL = aucun droit';
COMMENT ON COLUMN public.user_entry.premium_until IS 'Échéance du droit Premium ; NULL avec une source = sans échéance';
