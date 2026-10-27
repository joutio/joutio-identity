-- Joutio identity — baseline schema (squashed from V1..V5)
-- Generated from a fresh PostgreSQL 16 database after applying the legacy migrations.

--
-- PostgreSQL database dump
--


-- Dumped from database version 16.13
-- Dumped by pg_dump version 16.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: account_deletion_audit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.account_deletion_audit (
    deletion_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    requested_at timestamp without time zone NOT NULL,
    completed_at timestamp without time zone,
    status character varying(20) NOT NULL,
    CONSTRAINT chk_account_deletion_status CHECK (((status)::text = ANY ((ARRAY['REQUESTED'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying])::text[])))
);


--
-- Name: TABLE account_deletion_audit; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.account_deletion_audit IS 'Audit de suppression sans donnée personnelle';


--
-- Name: account_deletion_code; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.account_deletion_code (
    email character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    code_hash character varying(255) NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone NOT NULL,
    consumed_at timestamp without time zone
);


--
-- Name: TABLE account_deletion_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.account_deletion_code IS 'Codes OTP de confirmation de suppression de compte';


--
-- Name: email_claim; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.email_claim (
    email character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL
);


--
-- Name: TABLE email_claim; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.email_claim IS 'Réservation unique d''un email lors de l''inscription (anti-doublon concurrent)';


--
-- Name: COLUMN email_claim.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.email_claim.email IS 'Email normalisé (minuscules)';


--
-- Name: COLUMN email_claim.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.email_claim.user_id IS 'Identifiant de l''utilisateur ayant réservé l''email';


--
-- Name: oauth2_authorization; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauth2_authorization (
    id character varying(100) NOT NULL,
    registered_client_id character varying(100) NOT NULL,
    principal_name character varying(200) NOT NULL,
    authorization_grant_type character varying(100) NOT NULL,
    authorized_scopes character varying(1000) DEFAULT NULL::character varying,
    attributes text,
    state character varying(500) DEFAULT NULL::character varying,
    authorization_code_value text,
    authorization_code_issued_at timestamp with time zone,
    authorization_code_expires_at timestamp with time zone,
    authorization_code_metadata text,
    access_token_value text,
    access_token_issued_at timestamp with time zone,
    access_token_expires_at timestamp with time zone,
    access_token_metadata text,
    access_token_type character varying(100) DEFAULT NULL::character varying,
    access_token_scopes character varying(1000) DEFAULT NULL::character varying,
    oidc_id_token_value text,
    oidc_id_token_issued_at timestamp with time zone,
    oidc_id_token_expires_at timestamp with time zone,
    oidc_id_token_metadata text,
    refresh_token_value text,
    refresh_token_issued_at timestamp with time zone,
    refresh_token_expires_at timestamp with time zone,
    refresh_token_metadata text,
    user_code_value text,
    user_code_issued_at timestamp with time zone,
    user_code_expires_at timestamp with time zone,
    user_code_metadata text,
    device_code_value text,
    device_code_issued_at timestamp with time zone,
    device_code_expires_at timestamp with time zone,
    device_code_metadata text
);


--
-- Name: TABLE oauth2_authorization; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.oauth2_authorization IS 'Autorisations/codes/tokens OAuth2 persistés (partagés entre instances)';


--
-- Name: oauth2_authorization_consent; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauth2_authorization_consent (
    registered_client_id character varying(100) NOT NULL,
    principal_name character varying(200) NOT NULL,
    authorities character varying(1000) NOT NULL
);


--
-- Name: TABLE oauth2_authorization_consent; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.oauth2_authorization_consent IS 'Consentements OAuth2 persistés (partagés entre instances)';


--
-- Name: oauth2_registered_client; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauth2_registered_client (
    id character varying(100) NOT NULL,
    client_id character varying(100) NOT NULL,
    client_id_issued_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret character varying(200) DEFAULT NULL::character varying,
    client_secret_expires_at timestamp with time zone,
    client_name character varying(200) NOT NULL,
    client_authentication_methods character varying(1000) NOT NULL,
    authorization_grant_types character varying(1000) NOT NULL,
    redirect_uris character varying(1000) DEFAULT NULL::character varying,
    post_logout_redirect_uris character varying(1000) DEFAULT NULL::character varying,
    scopes character varying(1000) NOT NULL,
    client_settings character varying(2000) NOT NULL,
    token_settings character varying(2000) NOT NULL
);


--
-- Name: TABLE oauth2_registered_client; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.oauth2_registered_client IS 'Clients OAuth2 persistés (partagés entre instances)';


--
-- Name: spring_session; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.spring_session (
    primary_id character(36) NOT NULL,
    session_id character(36) NOT NULL,
    creation_time bigint NOT NULL,
    last_access_time bigint NOT NULL,
    max_inactive_interval integer NOT NULL,
    expiry_time bigint NOT NULL,
    principal_name character varying(100)
);


--
-- Name: spring_session_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.spring_session_attributes (
    session_primary_id character(36) NOT NULL,
    attribute_name character varying(200) NOT NULL,
    attribute_bytes bytea NOT NULL
);


--
-- Name: user_entry; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_entry (
    user_id character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    status character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    suspension_id character varying(255),
    suspension_reason character varying(40),
    suspension_note character varying(500),
    suspended_by character varying(255),
    suspended_at timestamp without time zone,
    suspended_until timestamp without time zone,
    reinstated_at timestamp without time zone,
    reinstated_by character varying(255),
    reinstatement_reason character varying(20),
    reinstatement_note character varying(500),
    CONSTRAINT chk_user_entry_status CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'SUSPENDED'::character varying, 'DELETED'::character varying])::text[])))
);


--
-- Name: TABLE user_entry; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.user_entry IS 'Table des utilisateurs - projection read-only mise à jour via Event Handlers';


--
-- Name: COLUMN user_entry.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_entry.user_id IS 'Identifiant unique de l''utilisateur (UUID)';


--
-- Name: COLUMN user_entry.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_entry.email IS 'Email unique de l''utilisateur';


--
-- Name: COLUMN user_entry.created_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_entry.created_at IS 'Date de création du compte';


--
-- Name: COLUMN user_entry.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_entry.status IS 'Statut du compte : ACTIVE ou SUSPENDED';


--
-- Name: COLUMN user_entry.suspension_note; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_entry.suspension_note IS 'Note interne de modération (jamais exposée au joueur)';


--
-- Name: COLUMN user_entry.suspended_until; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_entry.suspended_until IS 'Échéance de la suspension ; NULL = suspension définitive';


--
-- Name: user_login_code; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_login_code (
    email character varying(255) NOT NULL,
    code_hash character varying(255) NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone NOT NULL,
    consumed_at timestamp without time zone
);


--
-- Name: TABLE user_login_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.user_login_code IS 'Codes de connexion à usage unique (OTP email) - un seul actif par email';


--
-- Name: COLUMN user_login_code.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_login_code.email IS 'Email destinataire (non normalisé côté SQL, normalisé côté applicatif)';


--
-- Name: COLUMN user_login_code.code_hash; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_login_code.code_hash IS 'Hash BCrypt du code à 6 chiffres';


--
-- Name: COLUMN user_login_code.attempts; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_login_code.attempts IS 'Nombre de tentatives de vérification échouées';


--
-- Name: COLUMN user_login_code.expires_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_login_code.expires_at IS 'Date d''expiration du code';


--
-- Name: COLUMN user_login_code.consumed_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_login_code.consumed_at IS 'Date de consommation (usage unique)';


--
-- Name: user_report; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_report (
    report_id character varying(255) NOT NULL,
    thread_id character varying(255) NOT NULL,
    target_user_id character varying(255) NOT NULL,
    reporter_id character varying(255) NOT NULL,
    comment character varying(500),
    context character varying(20) NOT NULL,
    game_id character varying(255),
    target_pseudonym character varying(100),
    target_avatar_options text,
    status character varying(20) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    reviewed_by character varying(255),
    reviewed_at timestamp without time zone,
    resolution_note character varying(500),
    CONSTRAINT chk_user_report_context CHECK (((context)::text = ANY ((ARRAY['PROFILE'::character varying, 'GAME'::character varying])::text[]))),
    CONSTRAINT chk_user_report_status CHECK (((status)::text = ANY ((ARRAY['OPEN'::character varying, 'RESOLVED'::character varying, 'DISMISSED'::character varying])::text[])))
);


--
-- Name: TABLE user_report; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.user_report IS 'Signalements de joueurs (file de modération admin)';


--
-- Name: user_report_reason; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_report_reason (
    report_id character varying(255) NOT NULL,
    reason character varying(32) NOT NULL,
    CONSTRAINT chk_user_report_reason CHECK (((reason)::text = ANY ((ARRAY['HARASSMENT'::character varying, 'HATE_SPEECH'::character varying, 'INAPPROPRIATE_CONTENT'::character varying, 'CHEATING'::character varying, 'SPAM'::character varying, 'OTHER'::character varying])::text[])))
);


--
-- Name: TABLE user_report_reason; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.user_report_reason IS 'Motifs d''un signalement de joueur';


--
-- Name: user_social_providers_entry; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_social_providers_entry (
    user_id character varying(255) NOT NULL,
    provider character varying(50) NOT NULL
);


--
-- Name: TABLE user_social_providers_entry; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.user_social_providers_entry IS 'Providers sociaux liés au compte utilisateur (Google, Facebook, etc.)';


--
-- Name: COLUMN user_social_providers_entry.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_social_providers_entry.user_id IS 'Référence vers user_entry';


--
-- Name: COLUMN user_social_providers_entry.provider; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.user_social_providers_entry.provider IS 'Provider social (GOOGLE, FACEBOOK, TWITTER, GITHUB, LINKEDIN)';


--
-- Name: account_deletion_audit account_deletion_audit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account_deletion_audit
    ADD CONSTRAINT account_deletion_audit_pkey PRIMARY KEY (deletion_id);


--
-- Name: account_deletion_code account_deletion_code_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account_deletion_code
    ADD CONSTRAINT account_deletion_code_pkey PRIMARY KEY (email);


--
-- Name: email_claim email_claim_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.email_claim
    ADD CONSTRAINT email_claim_pkey PRIMARY KEY (email);


--
-- Name: oauth2_authorization_consent oauth2_authorization_consent_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauth2_authorization_consent
    ADD CONSTRAINT oauth2_authorization_consent_pkey PRIMARY KEY (registered_client_id, principal_name);


--
-- Name: oauth2_authorization oauth2_authorization_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauth2_authorization
    ADD CONSTRAINT oauth2_authorization_pkey PRIMARY KEY (id);


--
-- Name: oauth2_registered_client oauth2_registered_client_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauth2_registered_client
    ADD CONSTRAINT oauth2_registered_client_pkey PRIMARY KEY (id);


--
-- Name: user_social_providers_entry pk_user_social_providers_entry; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_social_providers_entry
    ADD CONSTRAINT pk_user_social_providers_entry PRIMARY KEY (user_id, provider);


--
-- Name: spring_session_attributes spring_session_attributes_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spring_session_attributes
    ADD CONSTRAINT spring_session_attributes_pk PRIMARY KEY (session_primary_id, attribute_name);


--
-- Name: spring_session spring_session_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spring_session
    ADD CONSTRAINT spring_session_pk PRIMARY KEY (primary_id);


--
-- Name: user_entry user_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_entry
    ADD CONSTRAINT user_entry_pkey PRIMARY KEY (user_id);


--
-- Name: user_login_code user_login_code_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_login_code
    ADD CONSTRAINT user_login_code_pkey PRIMARY KEY (email);


--
-- Name: user_report user_report_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_report
    ADD CONSTRAINT user_report_pkey PRIMARY KEY (report_id);


--
-- Name: user_report_reason user_report_reason_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_report_reason
    ADD CONSTRAINT user_report_reason_pkey PRIMARY KEY (report_id, reason);


--
-- Name: idx_account_deletion_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_account_deletion_user ON public.account_deletion_audit USING btree (user_id);


--
-- Name: idx_user_entry_email; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_entry_email ON public.user_entry USING btree (email);


--
-- Name: idx_user_entry_status_until; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_entry_status_until ON public.user_entry USING btree (status, suspended_until);


--
-- Name: idx_user_report_status_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_report_status_created ON public.user_report USING btree (status, created_at);


--
-- Name: idx_user_report_target_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_report_target_status ON public.user_report USING btree (target_user_id, status);


--
-- Name: idx_user_report_thread; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_report_thread ON public.user_report USING btree (thread_id);


--
-- Name: idx_user_social_providers_entry_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_social_providers_entry_user_id ON public.user_social_providers_entry USING btree (user_id);


--
-- Name: spring_session_ix1; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX spring_session_ix1 ON public.spring_session USING btree (session_id);


--
-- Name: spring_session_ix2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX spring_session_ix2 ON public.spring_session USING btree (expiry_time);


--
-- Name: spring_session_ix3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX spring_session_ix3 ON public.spring_session USING btree (principal_name);


--
-- Name: uq_user_entry_active_email; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_user_entry_active_email ON public.user_entry USING btree (email) WHERE ((status)::text <> 'DELETED'::text);


--
-- Name: uq_user_report_open_thread; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_user_report_open_thread ON public.user_report USING btree (thread_id) WHERE ((status)::text = 'OPEN'::text);


--
-- Name: user_report_reason fk_user_report_reason_report; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_report_reason
    ADD CONSTRAINT fk_user_report_reason_report FOREIGN KEY (report_id) REFERENCES public.user_report(report_id) ON DELETE CASCADE;


--
-- Name: user_report fk_user_report_reporter; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_report
    ADD CONSTRAINT fk_user_report_reporter FOREIGN KEY (reporter_id) REFERENCES public.user_entry(user_id) ON DELETE CASCADE;


--
-- Name: user_report fk_user_report_target; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_report
    ADD CONSTRAINT fk_user_report_target FOREIGN KEY (target_user_id) REFERENCES public.user_entry(user_id) ON DELETE CASCADE;


--
-- Name: user_social_providers_entry fk_user_social_providers_entry_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_social_providers_entry
    ADD CONSTRAINT fk_user_social_providers_entry_user FOREIGN KEY (user_id) REFERENCES public.user_entry(user_id) ON DELETE CASCADE;


--
-- Name: spring_session_attributes spring_session_attributes_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spring_session_attributes
    ADD CONSTRAINT spring_session_attributes_fk FOREIGN KEY (session_primary_id) REFERENCES public.spring_session(primary_id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--


