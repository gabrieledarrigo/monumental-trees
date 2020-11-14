SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE TYPE public.status AS ENUM (
    'ISCRITTO_IN_ELENCO',
    'RIMOSSO_PER_ABBATTIMENTO',
    'RIMOSSO_PER_MORTE_NATURALE',
    'RIMOSSO_PER_PERDITA_REQUISITI'
);

CREATE TYPE public.typology AS ENUM (
    'ALBERO_SINGOLO',
    'GRUPPO',
    'GRUPPO_PLURISPEFICIFO',
    'FILARE_SINGOLO',
    'FILARE_DOPPIO',
    'VIALE_ALBERATO'
);

CREATE TYPE public.context AS ENUM (
    'URBANO',
    'EXTRA_URBANO'
);

CREATE TABLE IF NOT EXISTS public.monumental_tree (
    id SERIAL CONSTRAINT monumental_tree_pkey PRIMARY KEY NOT NULL,
    status public.status NOT NULL,
    point_id character varying(255) CONSTRAINT monumental_tree_point_id_unique_key UNIQUE NOT NULL,
    typology public.typology NOT NULL,
    region character varying(512) NOT NULL,
    province character varying(512) NOT NULL,
    locality character varying(1024) NOT NULL,
    place character varying(1024) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    altitude double precision NOT NULL,
    genre character varying(1024) NOT NULL,
    scientific_name character varying(1024) NOT NULL,
    common_name character varying(1024) NOT NULL,
    context public.context NOT NULL,
    age_criteria boolean DEFAULT false NOT NULL,
    circumference_criteria boolean DEFAULT false NOT NULL,
    height_criteria boolean DEFAULT false NOT NULL,
    crown_criteria boolean DEFAULT false NOT NULL,
    shape_criteria boolean DEFAULT false NOT NULL,
    ecological_criteria boolean DEFAULT false NOT NULL,
    botanic_criteria boolean DEFAULT false NOT NULL,
    architecture_criteria boolean DEFAULT false NOT NULL,
    landscape_criteria boolean DEFAULT false NOT NULL,
    historical_criteria boolean DEFAULT false NOT NULL,
    height double precision,
    circumference double precision,
    average_group_height double precision,
    max_group_height double precision,
    average_group_circumference double precision,
    max_group_circumference double precision,
    decree character varying(1024),
    additional_decree character varying(1024),
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);