-- Version du compte surveillé par les caches du BFF (WatchUsersQuery) : un compteur par compte, incrémenté à chaque
-- écriture du compte.
CREATE TABLE public.live_version (
    user_id character varying(255) PRIMARY KEY,
    version bigint NOT NULL
);

COMMENT ON TABLE public.live_version IS 'Version du compte surveillé par les caches du BFF (WatchUsersQuery)';
