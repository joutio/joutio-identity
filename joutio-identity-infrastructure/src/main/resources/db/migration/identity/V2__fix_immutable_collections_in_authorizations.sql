-- Sessions natives (mobile) émises avant le correctif de NativeTokenIssuer : le claim `scope` y était stocké comme
-- ensemble immuable Java (`ImmutableCollections$Set12` / `$SetN`), refusé par l'allowlist Jackson de Spring Security
-- à la relecture. Résultat : chaque refresh répondait 500. Même format JSON pour un HashSet / ArrayList : on remplace
-- le nom de classe pour réparer ces sessions sans forcer une reconnexion.
UPDATE oauth2_authorization
SET access_token_metadata = replace(replace(replace(replace(access_token_metadata,
        '"java.util.ImmutableCollections$Set12"', '"java.util.HashSet"'),
        '"java.util.ImmutableCollections$SetN"', '"java.util.HashSet"'),
        '"java.util.ImmutableCollections$List12"', '"java.util.ArrayList"'),
        '"java.util.ImmutableCollections$ListN"', '"java.util.ArrayList"')
WHERE access_token_metadata LIKE '%java.util.ImmutableCollections$%';

UPDATE oauth2_authorization
SET attributes = replace(replace(replace(replace(attributes,
        '"java.util.ImmutableCollections$Set12"', '"java.util.HashSet"'),
        '"java.util.ImmutableCollections$SetN"', '"java.util.HashSet"'),
        '"java.util.ImmutableCollections$List12"', '"java.util.ArrayList"'),
        '"java.util.ImmutableCollections$ListN"', '"java.util.ArrayList"')
WHERE attributes LIKE '%java.util.ImmutableCollections$%';
