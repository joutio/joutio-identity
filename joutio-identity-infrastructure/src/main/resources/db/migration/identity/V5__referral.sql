-- Parrainage (design/specs/screens/partage.md § 6) : code stable par joueur et rattachement filleul → parrain.
CREATE TABLE public.referral_code (
    user_id character varying(255) NOT NULL PRIMARY KEY,
    code character varying(6) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    CONSTRAINT uk_referral_code_code UNIQUE (code)
);

CREATE TABLE public.referral_entry (
    user_id character varying(255) NOT NULL PRIMARY KEY,
    referrer_id character varying(255) NOT NULL,
    referred_at timestamp without time zone NOT NULL
);

CREATE INDEX idx_referral_entry_referrer ON public.referral_entry USING btree (referrer_id);
