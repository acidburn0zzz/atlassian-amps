--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: AO_21D670_WHITELIST_RULES; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_21D670_WHITELIST_RULES" (
    "ALLOWINBOUND" boolean,
    "EXPRESSION" text NOT NULL,
    "ID" integer NOT NULL,
    "TYPE" character varying(255) NOT NULL
);


ALTER TABLE public."AO_21D670_WHITELIST_RULES" OWNER TO postgres;

--
-- Name: AO_21D670_WHITELIST_RULES_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_21D670_WHITELIST_RULES_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_21D670_WHITELIST_RULES_ID_seq" OWNER TO postgres;

--
-- Name: AO_21D670_WHITELIST_RULES_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_21D670_WHITELIST_RULES_ID_seq" OWNED BY "AO_21D670_WHITELIST_RULES"."ID";


--
-- Name: AO_4AEACD_WEBHOOK_DAO; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_4AEACD_WEBHOOK_DAO" (
    "ENABLED" boolean,
    "ENCODED_EVENTS" text,
    "FILTER" text,
    "ID" integer NOT NULL,
    "JQL" character varying(255),
    "LAST_UPDATED" timestamp without time zone NOT NULL,
    "LAST_UPDATED_USER" character varying(255) NOT NULL,
    "NAME" text NOT NULL,
    "REGISTRATION_METHOD" character varying(255) NOT NULL,
    "URL" text NOT NULL,
    "PARAMETERS" text,
    "EXCLUDE_ISSUE_DETAILS" boolean
);


ALTER TABLE public."AO_4AEACD_WEBHOOK_DAO" OWNER TO postgres;

--
-- Name: AO_4AEACD_WEBHOOK_DAO_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_4AEACD_WEBHOOK_DAO_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_4AEACD_WEBHOOK_DAO_ID_seq" OWNER TO postgres;

--
-- Name: AO_4AEACD_WEBHOOK_DAO_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_4AEACD_WEBHOOK_DAO_ID_seq" OWNED BY "AO_4AEACD_WEBHOOK_DAO"."ID";


--
-- Name: AO_563AEE_ACTIVITY_ENTITY; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_563AEE_ACTIVITY_ENTITY" (
    "ACTIVITY_ID" bigint NOT NULL,
    "ACTOR_ID" integer,
    "CONTENT" text,
    "GENERATOR_DISPLAY_NAME" character varying(255),
    "GENERATOR_ID" character varying(767),
    "ICON_ID" integer,
    "ID" character varying(767),
    "ISSUE_KEY" character varying(255),
    "OBJECT_ID" integer,
    "POSTER" character varying(255),
    "PROJECT_KEY" character varying(255),
    "PUBLISHED" timestamp without time zone,
    "TARGET_ID" integer,
    "TITLE" character varying(255),
    "URL" character varying(767),
    "USERNAME" character varying(255),
    "VERB" character varying(767)
);


ALTER TABLE public."AO_563AEE_ACTIVITY_ENTITY" OWNER TO postgres;

--
-- Name: AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq" OWNER TO postgres;

--
-- Name: AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq" OWNED BY "AO_563AEE_ACTIVITY_ENTITY"."ACTIVITY_ID";


--
-- Name: AO_563AEE_ACTOR_ENTITY; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_563AEE_ACTOR_ENTITY" (
    "FULL_NAME" character varying(255),
    "ID" integer NOT NULL,
    "PROFILE_PAGE_URI" character varying(767),
    "PROFILE_PICTURE_URI" character varying(767),
    "USERNAME" character varying(255)
);


ALTER TABLE public."AO_563AEE_ACTOR_ENTITY" OWNER TO postgres;

--
-- Name: AO_563AEE_ACTOR_ENTITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_563AEE_ACTOR_ENTITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_563AEE_ACTOR_ENTITY_ID_seq" OWNER TO postgres;

--
-- Name: AO_563AEE_ACTOR_ENTITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_563AEE_ACTOR_ENTITY_ID_seq" OWNED BY "AO_563AEE_ACTOR_ENTITY"."ID";


--
-- Name: AO_563AEE_MEDIA_LINK_ENTITY; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_563AEE_MEDIA_LINK_ENTITY" (
    "DURATION" integer,
    "HEIGHT" integer,
    "ID" integer NOT NULL,
    "URL" character varying(767),
    "WIDTH" integer
);


ALTER TABLE public."AO_563AEE_MEDIA_LINK_ENTITY" OWNER TO postgres;

--
-- Name: AO_563AEE_MEDIA_LINK_ENTITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_563AEE_MEDIA_LINK_ENTITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_563AEE_MEDIA_LINK_ENTITY_ID_seq" OWNER TO postgres;

--
-- Name: AO_563AEE_MEDIA_LINK_ENTITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_563AEE_MEDIA_LINK_ENTITY_ID_seq" OWNED BY "AO_563AEE_MEDIA_LINK_ENTITY"."ID";


--
-- Name: AO_563AEE_OBJECT_ENTITY; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_563AEE_OBJECT_ENTITY" (
    "CONTENT" character varying(255),
    "DISPLAY_NAME" character varying(255),
    "ID" integer NOT NULL,
    "IMAGE_ID" integer,
    "OBJECT_ID" character varying(767),
    "OBJECT_TYPE" character varying(767),
    "SUMMARY" character varying(255),
    "URL" character varying(767)
);


ALTER TABLE public."AO_563AEE_OBJECT_ENTITY" OWNER TO postgres;

--
-- Name: AO_563AEE_OBJECT_ENTITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_563AEE_OBJECT_ENTITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_563AEE_OBJECT_ENTITY_ID_seq" OWNER TO postgres;

--
-- Name: AO_563AEE_OBJECT_ENTITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_563AEE_OBJECT_ENTITY_ID_seq" OWNED BY "AO_563AEE_OBJECT_ENTITY"."ID";


--
-- Name: AO_563AEE_TARGET_ENTITY; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_563AEE_TARGET_ENTITY" (
    "CONTENT" character varying(255),
    "DISPLAY_NAME" character varying(255),
    "ID" integer NOT NULL,
    "IMAGE_ID" integer,
    "OBJECT_ID" character varying(767),
    "OBJECT_TYPE" character varying(767),
    "SUMMARY" character varying(255),
    "URL" character varying(767)
);


ALTER TABLE public."AO_563AEE_TARGET_ENTITY" OWNER TO postgres;

--
-- Name: AO_563AEE_TARGET_ENTITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_563AEE_TARGET_ENTITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_563AEE_TARGET_ENTITY_ID_seq" OWNER TO postgres;

--
-- Name: AO_563AEE_TARGET_ENTITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_563AEE_TARGET_ENTITY_ID_seq" OWNED BY "AO_563AEE_TARGET_ENTITY"."ID";


--
-- Name: AO_B9A0F0_APPLIED_TEMPLATE; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_B9A0F0_APPLIED_TEMPLATE" (
    "ID" integer NOT NULL,
    "PROJECT_ID" bigint DEFAULT 0,
    "PROJECT_TEMPLATE_MODULE_KEY" character varying(255),
    "PROJECT_TEMPLATE_WEB_ITEM_KEY" character varying(255)
);


ALTER TABLE public."AO_B9A0F0_APPLIED_TEMPLATE" OWNER TO postgres;

--
-- Name: AO_B9A0F0_APPLIED_TEMPLATE_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_B9A0F0_APPLIED_TEMPLATE_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_B9A0F0_APPLIED_TEMPLATE_ID_seq" OWNER TO postgres;

--
-- Name: AO_B9A0F0_APPLIED_TEMPLATE_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_B9A0F0_APPLIED_TEMPLATE_ID_seq" OWNED BY "AO_B9A0F0_APPLIED_TEMPLATE"."ID";


--
-- Name: AO_E8B6CC_BRANCH; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_BRANCH" (
    "ID" integer NOT NULL,
    "NAME" character varying(255),
    "REPOSITORY_ID" integer
);


ALTER TABLE public."AO_E8B6CC_BRANCH" OWNER TO postgres;

--
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_BRANCH_HEAD_MAPPING" (
    "BRANCH_NAME" character varying(255),
    "HEAD" character varying(255),
    "ID" integer NOT NULL,
    "REPOSITORY_ID" integer
);


ALTER TABLE public."AO_E8B6CC_BRANCH_HEAD_MAPPING" OWNER TO postgres;

--
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq" OWNED BY "AO_E8B6CC_BRANCH_HEAD_MAPPING"."ID";


--
-- Name: AO_E8B6CC_BRANCH_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_BRANCH_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_BRANCH_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_BRANCH_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_BRANCH_ID_seq" OWNED BY "AO_E8B6CC_BRANCH"."ID";


--
-- Name: AO_E8B6CC_CHANGESET_MAPPING; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_CHANGESET_MAPPING" (
    "AUTHOR" character varying(255),
    "AUTHOR_EMAIL" character varying(255),
    "BRANCH" character varying(255),
    "DATE" timestamp without time zone,
    "FILES_DATA" text,
    "FILE_COUNT" integer DEFAULT 0,
    "FILE_DETAILS_JSON" text,
    "ID" integer NOT NULL,
    "ISSUE_KEY" character varying(255),
    "MESSAGE" text,
    "NODE" character varying(255),
    "PARENTS_DATA" character varying(255),
    "PROJECT_KEY" character varying(255),
    "RAW_AUTHOR" character varying(255),
    "RAW_NODE" character varying(255),
    "REPOSITORY_ID" integer DEFAULT 0,
    "SMARTCOMMIT_AVAILABLE" boolean,
    "VERSION" integer
);


ALTER TABLE public."AO_E8B6CC_CHANGESET_MAPPING" OWNER TO postgres;

--
-- Name: AO_E8B6CC_CHANGESET_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_CHANGESET_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_CHANGESET_MAPPING_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_CHANGESET_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_CHANGESET_MAPPING_ID_seq" OWNED BY "AO_E8B6CC_CHANGESET_MAPPING"."ID";


--
-- Name: AO_E8B6CC_COMMIT; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_COMMIT" (
    "AUTHOR" character varying(255),
    "AUTHOR_AVATAR_URL" character varying(255),
    "DATE" timestamp without time zone NOT NULL,
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "MESSAGE" text,
    "NODE" character varying(255),
    "RAW_AUTHOR" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_COMMIT" OWNER TO postgres;

--
-- Name: AO_E8B6CC_COMMIT_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_COMMIT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_COMMIT_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_COMMIT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_COMMIT_ID_seq" OWNED BY "AO_E8B6CC_COMMIT"."ID";


--
-- Name: AO_E8B6CC_GIT_HUB_EVENT; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_GIT_HUB_EVENT" (
    "CREATED_AT" timestamp without time zone NOT NULL,
    "GIT_HUB_ID" character varying(255) DEFAULT '0'::character varying NOT NULL,
    "ID" integer NOT NULL,
    "REPOSITORY_ID" integer NOT NULL,
    "SAVE_POINT" boolean
);


ALTER TABLE public."AO_E8B6CC_GIT_HUB_EVENT" OWNER TO postgres;

--
-- Name: AO_E8B6CC_GIT_HUB_EVENT_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_GIT_HUB_EVENT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_GIT_HUB_EVENT_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_GIT_HUB_EVENT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_GIT_HUB_EVENT_ID_seq" OWNED BY "AO_E8B6CC_GIT_HUB_EVENT"."ID";


--
-- Name: AO_E8B6CC_ISSUE_MAPPING; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_ISSUE_MAPPING" (
    "ID" integer NOT NULL,
    "ISSUE_ID" character varying(255),
    "NODE" character varying(255),
    "PROJECT_KEY" character varying(255),
    "REPOSITORY_URI" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_ISSUE_MAPPING" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ISSUE_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_ISSUE_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_ISSUE_MAPPING_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ISSUE_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_ISSUE_MAPPING_ID_seq" OWNED BY "AO_E8B6CC_ISSUE_MAPPING"."ID";


--
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_ISSUE_MAPPING_V2" (
    "AUTHOR" character varying(255),
    "BRANCH" character varying(255),
    "DATE" timestamp without time zone,
    "FILES_DATA" text,
    "ID" integer NOT NULL,
    "ISSUE_ID" character varying(255),
    "MESSAGE" text,
    "NODE" character varying(255),
    "PARENTS_DATA" character varying(255),
    "RAW_AUTHOR" character varying(255),
    "RAW_NODE" character varying(255),
    "REPOSITORY_ID" integer DEFAULT 0,
    "VERSION" integer
);


ALTER TABLE public."AO_E8B6CC_ISSUE_MAPPING_V2" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq" OWNED BY "AO_E8B6CC_ISSUE_MAPPING_V2"."ID";


--
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_ISSUE_TO_BRANCH" (
    "BRANCH_ID" integer,
    "ID" integer NOT NULL,
    "ISSUE_KEY" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_ISSUE_TO_BRANCH" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq" OWNED BY "AO_E8B6CC_ISSUE_TO_BRANCH"."ID";


--
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_ISSUE_TO_CHANGESET" (
    "CHANGESET_ID" integer,
    "ID" integer NOT NULL,
    "ISSUE_KEY" character varying(255),
    "PROJECT_KEY" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_ISSUE_TO_CHANGESET" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq" OWNED BY "AO_E8B6CC_ISSUE_TO_CHANGESET"."ID";


--
-- Name: AO_E8B6CC_MESSAGE; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_MESSAGE" (
    "ADDRESS" character varying(255) NOT NULL,
    "ID" integer NOT NULL,
    "PAYLOAD" text NOT NULL,
    "PAYLOAD_TYPE" character varying(255) NOT NULL,
    "PRIORITY" integer DEFAULT 0 NOT NULL
);


ALTER TABLE public."AO_E8B6CC_MESSAGE" OWNER TO postgres;

--
-- Name: AO_E8B6CC_MESSAGE_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_MESSAGE_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_MESSAGE_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_MESSAGE_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_MESSAGE_ID_seq" OWNED BY "AO_E8B6CC_MESSAGE"."ID";


--
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_MESSAGE_QUEUE_ITEM" (
    "ID" integer NOT NULL,
    "LAST_FAILED" timestamp without time zone,
    "MESSAGE_ID" integer NOT NULL,
    "QUEUE" character varying(255) NOT NULL,
    "RETRIES_COUNT" integer DEFAULT 0 NOT NULL,
    "STATE" character varying(255) NOT NULL,
    "STATE_INFO" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_MESSAGE_QUEUE_ITEM" OWNER TO postgres;

--
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq" OWNED BY "AO_E8B6CC_MESSAGE_QUEUE_ITEM"."ID";


--
-- Name: AO_E8B6CC_MESSAGE_TAG; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_MESSAGE_TAG" (
    "ID" integer NOT NULL,
    "MESSAGE_ID" integer,
    "TAG" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_MESSAGE_TAG" OWNER TO postgres;

--
-- Name: AO_E8B6CC_MESSAGE_TAG_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_MESSAGE_TAG_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_MESSAGE_TAG_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_MESSAGE_TAG_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_MESSAGE_TAG_ID_seq" OWNED BY "AO_E8B6CC_MESSAGE_TAG"."ID";


--
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_ORGANIZATION_MAPPING" (
    "ACCESS_TOKEN" character varying(255),
    "ADMIN_PASSWORD" character varying(255),
    "ADMIN_USERNAME" character varying(255),
    "AUTOLINK_NEW_REPOS" boolean,
    "DEFAULT_GROUPS_SLUGS" character varying(255),
    "DVCS_TYPE" character varying(255),
    "HOST_URL" character varying(255),
    "ID" integer NOT NULL,
    "NAME" character varying(255),
    "OAUTH_KEY" character varying(255),
    "OAUTH_SECRET" character varying(255),
    "SMARTCOMMITS_FOR_NEW_REPOS" boolean
);


ALTER TABLE public."AO_E8B6CC_ORGANIZATION_MAPPING" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq" OWNED BY "AO_E8B6CC_ORGANIZATION_MAPPING"."ID";


--
-- Name: AO_E8B6CC_PROJECT_MAPPING; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_PROJECT_MAPPING" (
    "ID" integer NOT NULL,
    "PASSWORD" character varying(255),
    "PROJECT_KEY" character varying(255),
    "REPOSITORY_URI" character varying(255),
    "USERNAME" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_PROJECT_MAPPING" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PROJECT_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_PROJECT_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_PROJECT_MAPPING_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PROJECT_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_PROJECT_MAPPING_ID_seq" OWNED BY "AO_E8B6CC_PROJECT_MAPPING"."ID";


--
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_PROJECT_MAPPING_V2" (
    "ACCESS_TOKEN" character varying(255),
    "ADMIN_PASSWORD" character varying(255),
    "ADMIN_USERNAME" character varying(255),
    "ID" integer NOT NULL,
    "LAST_COMMIT_DATE" timestamp without time zone,
    "PROJECT_KEY" character varying(255),
    "REPOSITORY_NAME" character varying(255),
    "REPOSITORY_TYPE" character varying(255),
    "REPOSITORY_URL" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_PROJECT_MAPPING_V2" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq" OWNED BY "AO_E8B6CC_PROJECT_MAPPING_V2"."ID";


--
-- Name: AO_E8B6CC_PR_ISSUE_KEY; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_PR_ISSUE_KEY" (
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "ISSUE_KEY" character varying(255),
    "PULL_REQUEST_ID" integer DEFAULT 0
);


ALTER TABLE public."AO_E8B6CC_PR_ISSUE_KEY" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PR_ISSUE_KEY_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_PR_ISSUE_KEY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_PR_ISSUE_KEY_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PR_ISSUE_KEY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_PR_ISSUE_KEY_ID_seq" OWNED BY "AO_E8B6CC_PR_ISSUE_KEY"."ID";


--
-- Name: AO_E8B6CC_PR_PARTICIPANT; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_PR_PARTICIPANT" (
    "APPROVED" boolean,
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "PULL_REQUEST_ID" integer,
    "ROLE" character varying(255),
    "USERNAME" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_PR_PARTICIPANT" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PR_PARTICIPANT_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_PR_PARTICIPANT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_PR_PARTICIPANT_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PR_PARTICIPANT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_PR_PARTICIPANT_ID_seq" OWNED BY "AO_E8B6CC_PR_PARTICIPANT"."ID";


--
-- Name: AO_E8B6CC_PR_TO_COMMIT; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_PR_TO_COMMIT" (
    "COMMIT_ID" integer NOT NULL,
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "REQUEST_ID" integer NOT NULL
);


ALTER TABLE public."AO_E8B6CC_PR_TO_COMMIT" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PR_TO_COMMIT_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_PR_TO_COMMIT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_PR_TO_COMMIT_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PR_TO_COMMIT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_PR_TO_COMMIT_ID_seq" OWNED BY "AO_E8B6CC_PR_TO_COMMIT"."ID";


--
-- Name: AO_E8B6CC_PULL_REQUEST; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_PULL_REQUEST" (
    "AUTHOR" character varying(255),
    "COMMENT_COUNT" integer DEFAULT 0,
    "CREATED_ON" timestamp without time zone,
    "DESTINATION_BRANCH" character varying(255),
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "LAST_STATUS" character varying(255),
    "NAME" character varying(255),
    "REMOTE_ID" bigint,
    "SOURCE_BRANCH" character varying(255),
    "SOURCE_REPO" character varying(255),
    "TO_REPOSITORY_ID" integer DEFAULT 0,
    "UPDATED_ON" timestamp without time zone,
    "URL" character varying(255)
);


ALTER TABLE public."AO_E8B6CC_PULL_REQUEST" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PULL_REQUEST_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_PULL_REQUEST_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_PULL_REQUEST_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_PULL_REQUEST_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_PULL_REQUEST_ID_seq" OWNED BY "AO_E8B6CC_PULL_REQUEST"."ID";


--
-- Name: AO_E8B6CC_REPOSITORY_MAPPING; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_REPOSITORY_MAPPING" (
    "ACTIVITY_LAST_SYNC" timestamp without time zone,
    "DELETED" boolean,
    "FORK" boolean,
    "FORK_OF_NAME" character varying(255),
    "FORK_OF_OWNER" character varying(255),
    "FORK_OF_SLUG" character varying(255),
    "ID" integer NOT NULL,
    "LAST_CHANGESET_NODE" character varying(255),
    "LAST_COMMIT_DATE" timestamp without time zone,
    "LINKED" boolean,
    "LOGO" text,
    "NAME" character varying(255),
    "ORGANIZATION_ID" integer DEFAULT 0,
    "SLUG" character varying(255),
    "SMARTCOMMITS_ENABLED" boolean
);


ALTER TABLE public."AO_E8B6CC_REPOSITORY_MAPPING" OWNER TO postgres;

--
-- Name: AO_E8B6CC_REPOSITORY_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_REPOSITORY_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_REPOSITORY_MAPPING_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_REPOSITORY_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_REPOSITORY_MAPPING_ID_seq" OWNED BY "AO_E8B6CC_REPOSITORY_MAPPING"."ID";


--
-- Name: AO_E8B6CC_REPO_TO_CHANGESET; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_REPO_TO_CHANGESET" (
    "CHANGESET_ID" integer,
    "ID" integer NOT NULL,
    "REPOSITORY_ID" integer
);


ALTER TABLE public."AO_E8B6CC_REPO_TO_CHANGESET" OWNER TO postgres;

--
-- Name: AO_E8B6CC_REPO_TO_CHANGESET_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_REPO_TO_CHANGESET_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_REPO_TO_CHANGESET_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_REPO_TO_CHANGESET_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_REPO_TO_CHANGESET_ID_seq" OWNED BY "AO_E8B6CC_REPO_TO_CHANGESET"."ID";


--
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_SYNC_AUDIT_LOG" (
    "END_DATE" timestamp without time zone,
    "EXC_TRACE" text,
    "FIRST_REQUEST_DATE" timestamp without time zone,
    "FLIGHT_TIME_MS" integer DEFAULT 0,
    "ID" integer NOT NULL,
    "NUM_REQUESTS" integer DEFAULT 0,
    "REPO_ID" integer DEFAULT 0,
    "START_DATE" timestamp without time zone,
    "SYNC_STATUS" character varying(255),
    "SYNC_TYPE" character varying(255),
    "TOTAL_ERRORS" integer DEFAULT 0
);


ALTER TABLE public."AO_E8B6CC_SYNC_AUDIT_LOG" OWNER TO postgres;

--
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq" OWNED BY "AO_E8B6CC_SYNC_AUDIT_LOG"."ID";


--
-- Name: AO_E8B6CC_SYNC_EVENT; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "AO_E8B6CC_SYNC_EVENT" (
    "EVENT_CLASS" text NOT NULL,
    "EVENT_DATE" timestamp without time zone NOT NULL,
    "EVENT_JSON" text NOT NULL,
    "ID" integer NOT NULL,
    "REPO_ID" integer DEFAULT 0 NOT NULL
);


ALTER TABLE public."AO_E8B6CC_SYNC_EVENT" OWNER TO postgres;

--
-- Name: AO_E8B6CC_SYNC_EVENT_ID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "AO_E8B6CC_SYNC_EVENT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AO_E8B6CC_SYNC_EVENT_ID_seq" OWNER TO postgres;

--
-- Name: AO_E8B6CC_SYNC_EVENT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "AO_E8B6CC_SYNC_EVENT_ID_seq" OWNED BY "AO_E8B6CC_SYNC_EVENT"."ID";


--
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE app_user (
    id numeric(18,0) NOT NULL,
    user_key character varying(255),
    lower_user_name character varying(255)
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- Name: audit_changed_value; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE audit_changed_value (
    id numeric(18,0) NOT NULL,
    log_id numeric(18,0),
    name character varying(255),
    delta_from text,
    delta_to text
);


ALTER TABLE public.audit_changed_value OWNER TO postgres;

--
-- Name: audit_item; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE audit_item (
    id numeric(18,0) NOT NULL,
    log_id numeric(18,0),
    object_type character varying(60),
    object_id character varying(255),
    object_name character varying(255),
    object_parent_id character varying(255),
    object_parent_name character varying(255)
);


ALTER TABLE public.audit_item OWNER TO postgres;

--
-- Name: audit_log; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE audit_log (
    id numeric(18,0) NOT NULL,
    remote_address character varying(60),
    created timestamp with time zone,
    author_key character varying(255),
    summary character varying(255),
    category character varying(255),
    object_type character varying(60),
    object_id character varying(255),
    object_name character varying(255),
    object_parent_id character varying(255),
    object_parent_name character varying(255),
    author_type numeric(9,0),
    event_source_name character varying(255),
    search_field text
);


ALTER TABLE public.audit_log OWNER TO postgres;

--
-- Name: avatar; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE avatar (
    id numeric(18,0) NOT NULL,
    filename character varying(255),
    contenttype character varying(255),
    avatartype character varying(60),
    owner character varying(255),
    systemavatar numeric(9,0)
);


ALTER TABLE public.avatar OWNER TO postgres;

--
-- Name: changegroup; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE changegroup (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    author character varying(255),
    created timestamp with time zone
);


ALTER TABLE public.changegroup OWNER TO postgres;

--
-- Name: changeitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE changeitem (
    id numeric(18,0) NOT NULL,
    groupid numeric(18,0),
    fieldtype character varying(255),
    field character varying(255),
    oldvalue text,
    oldstring text,
    newvalue text,
    newstring text
);


ALTER TABLE public.changeitem OWNER TO postgres;

--
-- Name: clusterlockstatus; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE clusterlockstatus (
    id numeric(18,0) NOT NULL,
    lock_name character varying(255),
    locked_by_node character varying(60),
    update_time numeric(18,0)
);


ALTER TABLE public.clusterlockstatus OWNER TO postgres;

--
-- Name: clustermessage; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE clustermessage (
    id numeric(18,0) NOT NULL,
    source_node character varying(60),
    destination_node character varying(60),
    claimed_by_node character varying(60),
    message character varying(255),
    message_time timestamp with time zone
);


ALTER TABLE public.clustermessage OWNER TO postgres;

--
-- Name: clusternode; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE clusternode (
    node_id character varying(60) NOT NULL,
    node_state character varying(60)
);


ALTER TABLE public.clusternode OWNER TO postgres;

--
-- Name: clusternodeheartbeat; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE clusternodeheartbeat (
    node_id character varying(60) NOT NULL,
    heartbeat_time numeric(18,0)
);


ALTER TABLE public.clusternodeheartbeat OWNER TO postgres;

--
-- Name: columnlayout; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE columnlayout (
    id numeric(18,0) NOT NULL,
    username character varying(255),
    searchrequest numeric(18,0)
);


ALTER TABLE public.columnlayout OWNER TO postgres;

--
-- Name: columnlayoutitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE columnlayoutitem (
    id numeric(18,0) NOT NULL,
    columnlayout numeric(18,0),
    fieldidentifier character varying(255),
    horizontalposition numeric(18,0)
);


ALTER TABLE public.columnlayoutitem OWNER TO postgres;

--
-- Name: component; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE component (
    id numeric(18,0) NOT NULL,
    project numeric(18,0),
    cname character varying(255),
    description text,
    url character varying(255),
    lead character varying(255),
    assigneetype numeric(18,0)
);


ALTER TABLE public.component OWNER TO postgres;

--
-- Name: configurationcontext; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE configurationcontext (
    id numeric(18,0) NOT NULL,
    projectcategory numeric(18,0),
    project numeric(18,0),
    customfield character varying(255),
    fieldconfigscheme numeric(18,0)
);


ALTER TABLE public.configurationcontext OWNER TO postgres;

--
-- Name: customfield; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE customfield (
    id numeric(18,0) NOT NULL,
    customfieldtypekey character varying(255),
    customfieldsearcherkey character varying(255),
    cfname character varying(255),
    description text,
    defaultvalue character varying(255),
    fieldtype numeric(18,0),
    project numeric(18,0),
    issuetype character varying(255)
);


ALTER TABLE public.customfield OWNER TO postgres;

--
-- Name: customfieldoption; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE customfieldoption (
    id numeric(18,0) NOT NULL,
    customfield numeric(18,0),
    customfieldconfig numeric(18,0),
    parentoptionid numeric(18,0),
    sequence numeric(18,0),
    customvalue character varying(255),
    optiontype character varying(60),
    disabled character varying(60)
);


ALTER TABLE public.customfieldoption OWNER TO postgres;

--
-- Name: customfieldvalue; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE customfieldvalue (
    id numeric(18,0) NOT NULL,
    issue numeric(18,0),
    customfield numeric(18,0),
    parentkey character varying(255),
    stringvalue character varying(255),
    numbervalue double precision,
    textvalue text,
    datevalue timestamp with time zone,
    valuetype character varying(255)
);


ALTER TABLE public.customfieldvalue OWNER TO postgres;

--
-- Name: cwd_application; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_application (
    id numeric(18,0) NOT NULL,
    application_name character varying(255),
    lower_application_name character varying(255),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    active numeric(9,0),
    description character varying(255),
    application_type character varying(255),
    credential character varying(255)
);


ALTER TABLE public.cwd_application OWNER TO postgres;

--
-- Name: cwd_application_address; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_application_address (
    application_id numeric(18,0) NOT NULL,
    remote_address character varying(255) NOT NULL,
    encoded_address_binary character varying(255),
    remote_address_mask numeric(9,0)
);


ALTER TABLE public.cwd_application_address OWNER TO postgres;

--
-- Name: cwd_directory; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_directory (
    id numeric(18,0) NOT NULL,
    directory_name character varying(255),
    lower_directory_name character varying(255),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    active numeric(9,0),
    description character varying(255),
    impl_class character varying(255),
    lower_impl_class character varying(255),
    directory_type character varying(60),
    directory_position numeric(18,0)
);


ALTER TABLE public.cwd_directory OWNER TO postgres;

--
-- Name: cwd_directory_attribute; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_directory_attribute (
    directory_id numeric(18,0) NOT NULL,
    attribute_name character varying(255) NOT NULL,
    attribute_value character varying(255)
);


ALTER TABLE public.cwd_directory_attribute OWNER TO postgres;

--
-- Name: cwd_directory_operation; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_directory_operation (
    directory_id numeric(18,0) NOT NULL,
    operation_type character varying(60) NOT NULL
);


ALTER TABLE public.cwd_directory_operation OWNER TO postgres;

--
-- Name: cwd_group; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_group (
    id numeric(18,0) NOT NULL,
    group_name character varying(255),
    lower_group_name character varying(255),
    active numeric(9,0),
    local numeric(9,0),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    description character varying(255),
    lower_description character varying(255),
    group_type character varying(60),
    directory_id numeric(18,0)
);


ALTER TABLE public.cwd_group OWNER TO postgres;

--
-- Name: cwd_group_attributes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_group_attributes (
    id numeric(18,0) NOT NULL,
    group_id numeric(18,0),
    directory_id numeric(18,0),
    attribute_name character varying(255),
    attribute_value character varying(255),
    lower_attribute_value character varying(255)
);


ALTER TABLE public.cwd_group_attributes OWNER TO postgres;

--
-- Name: cwd_membership; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_membership (
    id numeric(18,0) NOT NULL,
    parent_id numeric(18,0),
    child_id numeric(18,0),
    membership_type character varying(60),
    group_type character varying(60),
    parent_name character varying(255),
    lower_parent_name character varying(255),
    child_name character varying(255),
    lower_child_name character varying(255),
    directory_id numeric(18,0)
);


ALTER TABLE public.cwd_membership OWNER TO postgres;

--
-- Name: cwd_user; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_user (
    id numeric(18,0) NOT NULL,
    directory_id numeric(18,0),
    user_name character varying(255),
    lower_user_name character varying(255),
    active numeric(9,0),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    first_name character varying(255),
    lower_first_name character varying(255),
    last_name character varying(255),
    lower_last_name character varying(255),
    display_name character varying(255),
    lower_display_name character varying(255),
    email_address character varying(255),
    lower_email_address character varying(255),
    credential character varying(255),
    deleted_externally numeric(9,0),
    external_id character varying(255)
);


ALTER TABLE public.cwd_user OWNER TO postgres;

--
-- Name: cwd_user_attributes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cwd_user_attributes (
    id numeric(18,0) NOT NULL,
    user_id numeric(18,0),
    directory_id numeric(18,0),
    attribute_name character varying(255),
    attribute_value character varying(255),
    lower_attribute_value character varying(255)
);


ALTER TABLE public.cwd_user_attributes OWNER TO postgres;

--
-- Name: draftworkflowscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE draftworkflowscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text,
    workflow_scheme_id numeric(18,0),
    last_modified_date timestamp with time zone,
    last_modified_user character varying(255)
);


ALTER TABLE public.draftworkflowscheme OWNER TO postgres;

--
-- Name: draftworkflowschemeentity; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE draftworkflowschemeentity (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    workflow character varying(255),
    issuetype character varying(255)
);


ALTER TABLE public.draftworkflowschemeentity OWNER TO postgres;

--
-- Name: entity_property; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE entity_property (
    id numeric(18,0) NOT NULL,
    entity_name character varying(255),
    entity_id numeric(18,0),
    property_key character varying(255),
    created timestamp with time zone,
    updated timestamp with time zone,
    json_value text
);


ALTER TABLE public.entity_property OWNER TO postgres;

--
-- Name: entity_property_index_document; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE entity_property_index_document (
    id numeric(18,0) NOT NULL,
    plugin_key character varying(255),
    module_key character varying(255),
    entity_key character varying(255),
    updated timestamp with time zone,
    document text
);


ALTER TABLE public.entity_property_index_document OWNER TO postgres;

--
-- Name: external_entities; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE external_entities (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    entitytype character varying(255)
);


ALTER TABLE public.external_entities OWNER TO postgres;

--
-- Name: externalgadget; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE externalgadget (
    id numeric(18,0) NOT NULL,
    gadget_xml text
);


ALTER TABLE public.externalgadget OWNER TO postgres;

--
-- Name: favouriteassociations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE favouriteassociations (
    id numeric(18,0) NOT NULL,
    username character varying(255),
    entitytype character varying(60),
    entityid numeric(18,0),
    sequence numeric(18,0)
);


ALTER TABLE public.favouriteassociations OWNER TO postgres;

--
-- Name: feature; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE feature (
    id numeric(18,0) NOT NULL,
    feature_name character varying(255),
    feature_type character varying(10),
    user_key character varying(255)
);


ALTER TABLE public.feature OWNER TO postgres;

--
-- Name: fieldconfigscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldconfigscheme (
    id numeric(18,0) NOT NULL,
    configname character varying(255),
    description text,
    fieldid character varying(60),
    customfield numeric(18,0)
);


ALTER TABLE public.fieldconfigscheme OWNER TO postgres;

--
-- Name: fieldconfigschemeissuetype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldconfigschemeissuetype (
    id numeric(18,0) NOT NULL,
    issuetype character varying(255),
    fieldconfigscheme numeric(18,0),
    fieldconfiguration numeric(18,0)
);


ALTER TABLE public.fieldconfigschemeissuetype OWNER TO postgres;

--
-- Name: fieldconfiguration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldconfiguration (
    id numeric(18,0) NOT NULL,
    configname character varying(255),
    description text,
    fieldid character varying(60),
    customfield numeric(18,0)
);


ALTER TABLE public.fieldconfiguration OWNER TO postgres;

--
-- Name: fieldlayout; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldlayout (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255),
    layout_type character varying(255),
    layoutscheme numeric(18,0)
);


ALTER TABLE public.fieldlayout OWNER TO postgres;

--
-- Name: fieldlayoutitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldlayoutitem (
    id numeric(18,0) NOT NULL,
    fieldlayout numeric(18,0),
    fieldidentifier character varying(255),
    description text,
    verticalposition numeric(18,0),
    ishidden character varying(60),
    isrequired character varying(60),
    renderertype character varying(255)
);


ALTER TABLE public.fieldlayoutitem OWNER TO postgres;

--
-- Name: fieldlayoutscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldlayoutscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


ALTER TABLE public.fieldlayoutscheme OWNER TO postgres;

--
-- Name: fieldlayoutschemeassociation; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldlayoutschemeassociation (
    id numeric(18,0) NOT NULL,
    issuetype character varying(255),
    project numeric(18,0),
    fieldlayoutscheme numeric(18,0)
);


ALTER TABLE public.fieldlayoutschemeassociation OWNER TO postgres;

--
-- Name: fieldlayoutschemeentity; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldlayoutschemeentity (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    issuetype character varying(255),
    fieldlayout numeric(18,0)
);


ALTER TABLE public.fieldlayoutschemeentity OWNER TO postgres;

--
-- Name: fieldscreen; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldscreen (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255)
);


ALTER TABLE public.fieldscreen OWNER TO postgres;

--
-- Name: fieldscreenlayoutitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldscreenlayoutitem (
    id numeric(18,0) NOT NULL,
    fieldidentifier character varying(255),
    sequence numeric(18,0),
    fieldscreentab numeric(18,0)
);


ALTER TABLE public.fieldscreenlayoutitem OWNER TO postgres;

--
-- Name: fieldscreenscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldscreenscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255)
);


ALTER TABLE public.fieldscreenscheme OWNER TO postgres;

--
-- Name: fieldscreenschemeitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldscreenschemeitem (
    id numeric(18,0) NOT NULL,
    operation numeric(18,0),
    fieldscreen numeric(18,0),
    fieldscreenscheme numeric(18,0)
);


ALTER TABLE public.fieldscreenschemeitem OWNER TO postgres;

--
-- Name: fieldscreentab; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fieldscreentab (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255),
    sequence numeric(18,0),
    fieldscreen numeric(18,0)
);


ALTER TABLE public.fieldscreentab OWNER TO postgres;

--
-- Name: fileattachment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fileattachment (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    mimetype character varying(255),
    filename character varying(255),
    created timestamp with time zone,
    filesize numeric(18,0),
    author character varying(255),
    zip numeric(9,0),
    thumbnailable numeric(9,0)
);


ALTER TABLE public.fileattachment OWNER TO postgres;

--
-- Name: filtersubscription; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE filtersubscription (
    id numeric(18,0) NOT NULL,
    filter_i_d numeric(18,0),
    username character varying(60),
    groupname character varying(60),
    last_run timestamp with time zone,
    email_on_empty character varying(10)
);


ALTER TABLE public.filtersubscription OWNER TO postgres;

--
-- Name: gadgetuserpreference; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gadgetuserpreference (
    id numeric(18,0) NOT NULL,
    portletconfiguration numeric(18,0),
    userprefkey character varying(255),
    userprefvalue text
);


ALTER TABLE public.gadgetuserpreference OWNER TO postgres;

--
-- Name: genericconfiguration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE genericconfiguration (
    id numeric(18,0) NOT NULL,
    datatype character varying(60),
    datakey character varying(60),
    xmlvalue text
);


ALTER TABLE public.genericconfiguration OWNER TO postgres;

--
-- Name: globalpermissionentry; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE globalpermissionentry (
    id numeric(18,0) NOT NULL,
    permission character varying(255),
    group_id character varying(255)
);


ALTER TABLE public.globalpermissionentry OWNER TO postgres;

--
-- Name: groupbase; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE groupbase (
    id numeric(18,0) NOT NULL,
    groupname character varying(255)
);


ALTER TABLE public.groupbase OWNER TO postgres;

--
-- Name: issuelink; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE issuelink (
    id numeric(18,0) NOT NULL,
    linktype numeric(18,0),
    source numeric(18,0),
    destination numeric(18,0),
    sequence numeric(18,0)
);


ALTER TABLE public.issuelink OWNER TO postgres;

--
-- Name: issuelinktype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE issuelinktype (
    id numeric(18,0) NOT NULL,
    linkname character varying(255),
    inward character varying(255),
    outward character varying(255),
    pstyle character varying(60)
);


ALTER TABLE public.issuelinktype OWNER TO postgres;

--
-- Name: issuesecurityscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE issuesecurityscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text,
    defaultlevel numeric(18,0)
);


ALTER TABLE public.issuesecurityscheme OWNER TO postgres;

--
-- Name: issuestatus; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE issuestatus (
    id character varying(60) NOT NULL,
    sequence numeric(18,0),
    pname character varying(60),
    description text,
    iconurl character varying(255),
    statuscategory numeric(18,0)
);


ALTER TABLE public.issuestatus OWNER TO postgres;

--
-- Name: issuetype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE issuetype (
    id character varying(60) NOT NULL,
    sequence numeric(18,0),
    pname character varying(60),
    pstyle character varying(60),
    description text,
    iconurl character varying(255),
    avatar numeric(18,0)
);


ALTER TABLE public.issuetype OWNER TO postgres;

--
-- Name: issuetypescreenscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE issuetypescreenscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255)
);


ALTER TABLE public.issuetypescreenscheme OWNER TO postgres;

--
-- Name: issuetypescreenschemeentity; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE issuetypescreenschemeentity (
    id numeric(18,0) NOT NULL,
    issuetype character varying(255),
    scheme numeric(18,0),
    fieldscreenscheme numeric(18,0)
);


ALTER TABLE public.issuetypescreenschemeentity OWNER TO postgres;

--
-- Name: jiraaction; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jiraaction (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    author character varying(255),
    actiontype character varying(255),
    actionlevel character varying(255),
    rolelevel numeric(18,0),
    actionbody text,
    created timestamp with time zone,
    updateauthor character varying(255),
    updated timestamp with time zone,
    actionnum numeric(18,0)
);


ALTER TABLE public.jiraaction OWNER TO postgres;

--
-- Name: jiradraftworkflows; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jiradraftworkflows (
    id numeric(18,0) NOT NULL,
    parentname character varying(255),
    descriptor text
);


ALTER TABLE public.jiradraftworkflows OWNER TO postgres;

--
-- Name: jiraeventtype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jiraeventtype (
    id numeric(18,0) NOT NULL,
    template_id numeric(18,0),
    name character varying(255),
    description text,
    event_type character varying(60)
);


ALTER TABLE public.jiraeventtype OWNER TO postgres;

--
-- Name: jiraissue; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jiraissue (
    id numeric(18,0) NOT NULL,
    pkey character varying(255),
    issuenum numeric(18,0),
    project numeric(18,0),
    reporter character varying(255),
    assignee character varying(255),
    creator character varying(255),
    issuetype character varying(255),
    summary character varying(255),
    description text,
    environment text,
    priority character varying(255),
    resolution character varying(255),
    issuestatus character varying(255),
    created timestamp with time zone,
    updated timestamp with time zone,
    duedate timestamp with time zone,
    resolutiondate timestamp with time zone,
    votes numeric(18,0),
    watches numeric(18,0),
    timeoriginalestimate numeric(18,0),
    timeestimate numeric(18,0),
    timespent numeric(18,0),
    workflow_id numeric(18,0),
    security numeric(18,0),
    fixfor numeric(18,0),
    component numeric(18,0)
);


ALTER TABLE public.jiraissue OWNER TO postgres;

--
-- Name: jiraperms; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jiraperms (
    id numeric(18,0) NOT NULL,
    permtype numeric(18,0),
    projectid numeric(18,0),
    groupname character varying(255)
);


ALTER TABLE public.jiraperms OWNER TO postgres;

--
-- Name: jiraworkflows; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jiraworkflows (
    id numeric(18,0) NOT NULL,
    workflowname character varying(255),
    creatorname character varying(255),
    descriptor text,
    islocked character varying(60)
);


ALTER TABLE public.jiraworkflows OWNER TO postgres;

--
-- Name: jquartz_blob_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_blob_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


ALTER TABLE public.jquartz_blob_triggers OWNER TO postgres;

--
-- Name: jquartz_calendars; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_calendars (
    sched_name character varying(120),
    calendar_name character varying(200) NOT NULL,
    calendar bytea
);


ALTER TABLE public.jquartz_calendars OWNER TO postgres;

--
-- Name: jquartz_cron_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_cron_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(120),
    time_zone_id character varying(80)
);


ALTER TABLE public.jquartz_cron_triggers OWNER TO postgres;

--
-- Name: jquartz_fired_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_fired_triggers (
    sched_name character varying(120),
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200),
    trigger_group character varying(200),
    is_volatile boolean,
    instance_name character varying(200),
    fired_time numeric(18,0),
    sched_time numeric(18,0),
    priority numeric(9,0),
    state character varying(16),
    job_name character varying(200),
    job_group character varying(200),
    is_stateful boolean,
    is_nonconcurrent boolean,
    is_update_data boolean,
    requests_recovery boolean
);


ALTER TABLE public.jquartz_fired_triggers OWNER TO postgres;

--
-- Name: jquartz_job_details; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_job_details (
    sched_name character varying(120),
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250),
    is_durable boolean,
    is_volatile boolean,
    is_stateful boolean,
    is_nonconcurrent boolean,
    is_update_data boolean,
    requests_recovery boolean,
    job_data bytea
);


ALTER TABLE public.jquartz_job_details OWNER TO postgres;

--
-- Name: jquartz_job_listeners; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_job_listeners (
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    job_listener character varying(200) NOT NULL
);


ALTER TABLE public.jquartz_job_listeners OWNER TO postgres;

--
-- Name: jquartz_locks; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_locks (
    sched_name character varying(120),
    lock_name character varying(40) NOT NULL
);


ALTER TABLE public.jquartz_locks OWNER TO postgres;

--
-- Name: jquartz_paused_trigger_grps; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_paused_trigger_grps (
    sched_name character varying(120),
    trigger_group character varying(200) NOT NULL
);


ALTER TABLE public.jquartz_paused_trigger_grps OWNER TO postgres;

--
-- Name: jquartz_scheduler_state; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_scheduler_state (
    sched_name character varying(120),
    instance_name character varying(200) NOT NULL,
    last_checkin_time numeric(18,0),
    checkin_interval numeric(18,0)
);


ALTER TABLE public.jquartz_scheduler_state OWNER TO postgres;

--
-- Name: jquartz_simple_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_simple_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count numeric(18,0),
    repeat_interval numeric(18,0),
    times_triggered numeric(18,0)
);


ALTER TABLE public.jquartz_simple_triggers OWNER TO postgres;

--
-- Name: jquartz_simprop_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_simprop_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 numeric(9,0),
    int_prop_2 numeric(9,0),
    long_prop_1 numeric(18,0),
    long_prop_2 numeric(18,0),
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean
);


ALTER TABLE public.jquartz_simprop_triggers OWNER TO postgres;

--
-- Name: jquartz_trigger_listeners; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_trigger_listeners (
    trigger_name character varying(200),
    trigger_group character varying(200) NOT NULL,
    trigger_listener character varying(200) NOT NULL
);


ALTER TABLE public.jquartz_trigger_listeners OWNER TO postgres;

--
-- Name: jquartz_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jquartz_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_volatile boolean,
    description character varying(250),
    next_fire_time numeric(18,0),
    prev_fire_time numeric(18,0),
    priority numeric(9,0),
    trigger_state character varying(16),
    trigger_type character varying(8),
    start_time numeric(18,0),
    end_time numeric(18,0),
    calendar_name character varying(200),
    misfire_instr numeric(4,0),
    job_data bytea
);


ALTER TABLE public.jquartz_triggers OWNER TO postgres;

--
-- Name: label; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE label (
    id numeric(18,0) NOT NULL,
    fieldid numeric(18,0),
    issue numeric(18,0),
    label character varying(255)
);


ALTER TABLE public.label OWNER TO postgres;

--
-- Name: licenserolesgroup; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE licenserolesgroup (
    id numeric(18,0) NOT NULL,
    license_role_name character varying(255),
    group_id character varying(255)
);


ALTER TABLE public.licenserolesgroup OWNER TO postgres;

--
-- Name: listenerconfig; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE listenerconfig (
    id numeric(18,0) NOT NULL,
    clazz character varying(255),
    listenername character varying(255)
);


ALTER TABLE public.listenerconfig OWNER TO postgres;

--
-- Name: mailserver; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mailserver (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text,
    mailfrom character varying(255),
    prefix character varying(60),
    smtp_port character varying(60),
    protocol character varying(60),
    server_type character varying(60),
    servername character varying(255),
    jndilocation character varying(255),
    mailusername character varying(255),
    mailpassword character varying(255),
    istlsrequired character varying(60),
    timeout numeric(18,0),
    socks_port character varying(60),
    socks_host character varying(60)
);


ALTER TABLE public.mailserver OWNER TO postgres;

--
-- Name: managedconfigurationitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE managedconfigurationitem (
    id numeric(18,0) NOT NULL,
    item_id character varying(255),
    item_type character varying(255),
    managed character varying(10),
    access_level character varying(255),
    source character varying(255),
    description_key character varying(255)
);


ALTER TABLE public.managedconfigurationitem OWNER TO postgres;

--
-- Name: membershipbase; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE membershipbase (
    id numeric(18,0) NOT NULL,
    user_name character varying(255),
    group_name character varying(255)
);


ALTER TABLE public.membershipbase OWNER TO postgres;

--
-- Name: moved_issue_key; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE moved_issue_key (
    id numeric(18,0) NOT NULL,
    old_issue_key character varying(255),
    issue_id numeric(18,0)
);


ALTER TABLE public.moved_issue_key OWNER TO postgres;

--
-- Name: nodeassociation; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE nodeassociation (
    source_node_id numeric(18,0) NOT NULL,
    source_node_entity character varying(60) NOT NULL,
    sink_node_id numeric(18,0) NOT NULL,
    sink_node_entity character varying(60) NOT NULL,
    association_type character varying(60) NOT NULL,
    sequence numeric(9,0)
);


ALTER TABLE public.nodeassociation OWNER TO postgres;

--
-- Name: nodeindexcounter; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE nodeindexcounter (
    id numeric(18,0) NOT NULL,
    node_id character varying(60),
    sending_node_id character varying(60),
    index_operation_id numeric(18,0)
);


ALTER TABLE public.nodeindexcounter OWNER TO postgres;

--
-- Name: notification; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE notification (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    event character varying(60),
    event_type_id numeric(18,0),
    template_id numeric(18,0),
    notif_type character varying(60),
    notif_parameter character varying(60)
);


ALTER TABLE public.notification OWNER TO postgres;

--
-- Name: notificationinstance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE notificationinstance (
    id numeric(18,0) NOT NULL,
    notificationtype character varying(60),
    source numeric(18,0),
    emailaddress character varying(255),
    messageid character varying(255)
);


ALTER TABLE public.notificationinstance OWNER TO postgres;

--
-- Name: notificationscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE notificationscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


ALTER TABLE public.notificationscheme OWNER TO postgres;

--
-- Name: oauthconsumer; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oauthconsumer (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    consumername character varying(255),
    consumer_key character varying(255),
    consumerservice character varying(255),
    public_key text,
    private_key text,
    description text,
    callback text,
    signature_method character varying(60),
    shared_secret text
);


ALTER TABLE public.oauthconsumer OWNER TO postgres;

--
-- Name: oauthconsumertoken; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oauthconsumertoken (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    token_key character varying(255),
    token character varying(255),
    token_secret character varying(255),
    token_type character varying(60),
    consumer_key character varying(255)
);


ALTER TABLE public.oauthconsumertoken OWNER TO postgres;

--
-- Name: oauthspconsumer; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oauthspconsumer (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    consumer_key character varying(255),
    consumername character varying(255),
    public_key text,
    description text,
    callback text,
    two_l_o_allowed character varying(60),
    executing_two_l_o_user character varying(255),
    two_l_o_impersonation_allowed character varying(60),
    three_l_o_allowed character varying(60)
);


ALTER TABLE public.oauthspconsumer OWNER TO postgres;

--
-- Name: oauthsptoken; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oauthsptoken (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    token character varying(255),
    token_secret character varying(255),
    token_type character varying(60),
    consumer_key character varying(255),
    username character varying(255),
    ttl numeric(18,0),
    spauth character varying(60),
    callback text,
    spverifier character varying(255),
    spversion character varying(60),
    session_handle character varying(255),
    session_creation_time timestamp with time zone,
    session_last_renewal_time timestamp with time zone,
    session_time_to_live timestamp with time zone
);


ALTER TABLE public.oauthsptoken OWNER TO postgres;

--
-- Name: optionconfiguration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE optionconfiguration (
    id numeric(18,0) NOT NULL,
    fieldid character varying(60),
    optionid character varying(60),
    fieldconfig numeric(18,0),
    sequence numeric(18,0)
);


ALTER TABLE public.optionconfiguration OWNER TO postgres;

--
-- Name: os_currentstep; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE os_currentstep (
    id numeric(18,0) NOT NULL,
    entry_id numeric(18,0),
    step_id numeric(9,0),
    action_id numeric(9,0),
    owner character varying(60),
    start_date timestamp with time zone,
    due_date timestamp with time zone,
    finish_date timestamp with time zone,
    status character varying(60),
    caller character varying(60)
);


ALTER TABLE public.os_currentstep OWNER TO postgres;

--
-- Name: os_currentstep_prev; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE os_currentstep_prev (
    id numeric(18,0) NOT NULL,
    previous_id numeric(18,0) NOT NULL
);


ALTER TABLE public.os_currentstep_prev OWNER TO postgres;

--
-- Name: os_historystep; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE os_historystep (
    id numeric(18,0) NOT NULL,
    entry_id numeric(18,0),
    step_id numeric(9,0),
    action_id numeric(9,0),
    owner character varying(60),
    start_date timestamp with time zone,
    due_date timestamp with time zone,
    finish_date timestamp with time zone,
    status character varying(60),
    caller character varying(60)
);


ALTER TABLE public.os_historystep OWNER TO postgres;

--
-- Name: os_historystep_prev; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE os_historystep_prev (
    id numeric(18,0) NOT NULL,
    previous_id numeric(18,0) NOT NULL
);


ALTER TABLE public.os_historystep_prev OWNER TO postgres;

--
-- Name: os_wfentry; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE os_wfentry (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    initialized numeric(9,0),
    state numeric(9,0)
);


ALTER TABLE public.os_wfentry OWNER TO postgres;

--
-- Name: permissionscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE permissionscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


ALTER TABLE public.permissionscheme OWNER TO postgres;

--
-- Name: pluginstate; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pluginstate (
    pluginkey character varying(255) NOT NULL,
    pluginenabled character varying(60)
);


ALTER TABLE public.pluginstate OWNER TO postgres;

--
-- Name: pluginversion; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pluginversion (
    id numeric(18,0) NOT NULL,
    pluginname character varying(255),
    pluginkey character varying(255),
    pluginversion character varying(255),
    created timestamp with time zone
);


ALTER TABLE public.pluginversion OWNER TO postgres;

--
-- Name: portalpage; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE portalpage (
    id numeric(18,0) NOT NULL,
    username character varying(255),
    pagename character varying(255),
    description character varying(255),
    sequence numeric(18,0),
    fav_count numeric(18,0),
    layout character varying(255),
    ppversion numeric(18,0)
);


ALTER TABLE public.portalpage OWNER TO postgres;

--
-- Name: portletconfiguration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE portletconfiguration (
    id numeric(18,0) NOT NULL,
    portalpage numeric(18,0),
    portlet_id character varying(255),
    column_number numeric(9,0),
    positionseq numeric(9,0),
    gadget_xml text,
    color character varying(255)
);


ALTER TABLE public.portletconfiguration OWNER TO postgres;

--
-- Name: priority; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE priority (
    id character varying(60) NOT NULL,
    sequence numeric(18,0),
    pname character varying(60),
    description text,
    iconurl character varying(255),
    status_color character varying(60)
);


ALTER TABLE public.priority OWNER TO postgres;

--
-- Name: productlicense; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE productlicense (
    id numeric(18,0) NOT NULL,
    license text
);


ALTER TABLE public.productlicense OWNER TO postgres;

--
-- Name: project; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE project (
    id numeric(18,0) NOT NULL,
    pname character varying(255),
    url character varying(255),
    lead character varying(255),
    description text,
    pkey character varying(255),
    pcounter numeric(18,0),
    assigneetype numeric(18,0),
    avatar numeric(18,0),
    originalkey character varying(255)
);


ALTER TABLE public.project OWNER TO postgres;

--
-- Name: project_key; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE project_key (
    id numeric(18,0) NOT NULL,
    project_id numeric(18,0),
    project_key character varying(255)
);


ALTER TABLE public.project_key OWNER TO postgres;

--
-- Name: projectcategory; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE projectcategory (
    id numeric(18,0) NOT NULL,
    cname character varying(255),
    description text
);


ALTER TABLE public.projectcategory OWNER TO postgres;

--
-- Name: projectrole; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE projectrole (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


ALTER TABLE public.projectrole OWNER TO postgres;

--
-- Name: projectroleactor; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE projectroleactor (
    id numeric(18,0) NOT NULL,
    pid numeric(18,0),
    projectroleid numeric(18,0),
    roletype character varying(255),
    roletypeparameter character varying(255)
);


ALTER TABLE public.projectroleactor OWNER TO postgres;

--
-- Name: projectversion; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE projectversion (
    id numeric(18,0) NOT NULL,
    project numeric(18,0),
    vname character varying(255),
    description text,
    sequence numeric(18,0),
    released character varying(10),
    archived character varying(10),
    url character varying(255),
    startdate timestamp with time zone,
    releasedate timestamp with time zone
);


ALTER TABLE public.projectversion OWNER TO postgres;

--
-- Name: propertydata; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE propertydata (
    id numeric(18,0) NOT NULL,
    propertyvalue oid
);


ALTER TABLE public.propertydata OWNER TO postgres;

--
-- Name: propertydate; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE propertydate (
    id numeric(18,0) NOT NULL,
    propertyvalue timestamp with time zone
);


ALTER TABLE public.propertydate OWNER TO postgres;

--
-- Name: propertydecimal; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE propertydecimal (
    id numeric(18,0) NOT NULL,
    propertyvalue double precision
);


ALTER TABLE public.propertydecimal OWNER TO postgres;

--
-- Name: propertyentry; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE propertyentry (
    id numeric(18,0) NOT NULL,
    entity_name character varying(255),
    entity_id numeric(18,0),
    property_key character varying(255),
    propertytype numeric(9,0)
);


ALTER TABLE public.propertyentry OWNER TO postgres;

--
-- Name: propertynumber; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE propertynumber (
    id numeric(18,0) NOT NULL,
    propertyvalue numeric(18,0)
);


ALTER TABLE public.propertynumber OWNER TO postgres;

--
-- Name: propertystring; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE propertystring (
    id numeric(18,0) NOT NULL,
    propertyvalue text
);


ALTER TABLE public.propertystring OWNER TO postgres;

--
-- Name: propertytext; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE propertytext (
    id numeric(18,0) NOT NULL,
    propertyvalue text
);


ALTER TABLE public.propertytext OWNER TO postgres;

--
-- Name: qrtz_calendars; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qrtz_calendars (
    id numeric(18,0),
    calendar_name character varying(255) NOT NULL,
    calendar text
);


ALTER TABLE public.qrtz_calendars OWNER TO postgres;

--
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qrtz_cron_triggers (
    id numeric(18,0) NOT NULL,
    trigger_id numeric(18,0),
    cronexperssion character varying(255)
);


ALTER TABLE public.qrtz_cron_triggers OWNER TO postgres;

--
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qrtz_fired_triggers (
    id numeric(18,0),
    entry_id character varying(255) NOT NULL,
    trigger_id numeric(18,0),
    trigger_listener character varying(255),
    fired_time timestamp with time zone,
    trigger_state character varying(255)
);


ALTER TABLE public.qrtz_fired_triggers OWNER TO postgres;

--
-- Name: qrtz_job_details; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qrtz_job_details (
    id numeric(18,0) NOT NULL,
    job_name character varying(255),
    job_group character varying(255),
    class_name character varying(255),
    is_durable character varying(60),
    is_stateful character varying(60),
    requests_recovery character varying(60),
    job_data character varying(255)
);


ALTER TABLE public.qrtz_job_details OWNER TO postgres;

--
-- Name: qrtz_job_listeners; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qrtz_job_listeners (
    id numeric(18,0) NOT NULL,
    job numeric(18,0),
    job_listener character varying(255)
);


ALTER TABLE public.qrtz_job_listeners OWNER TO postgres;

--
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qrtz_simple_triggers (
    id numeric(18,0) NOT NULL,
    trigger_id numeric(18,0),
    repeat_count numeric(9,0),
    repeat_interval numeric(18,0),
    times_triggered numeric(9,0)
);


ALTER TABLE public.qrtz_simple_triggers OWNER TO postgres;

--
-- Name: qrtz_trigger_listeners; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qrtz_trigger_listeners (
    id numeric(18,0) NOT NULL,
    trigger_id numeric(18,0),
    trigger_listener character varying(255)
);


ALTER TABLE public.qrtz_trigger_listeners OWNER TO postgres;

--
-- Name: qrtz_triggers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qrtz_triggers (
    id numeric(18,0) NOT NULL,
    trigger_name character varying(255),
    trigger_group character varying(255),
    job numeric(18,0),
    next_fire timestamp with time zone,
    trigger_state character varying(255),
    trigger_type character varying(60),
    start_time timestamp with time zone,
    end_time timestamp with time zone,
    calendar_name character varying(255),
    misfire_instr numeric(9,0)
);


ALTER TABLE public.qrtz_triggers OWNER TO postgres;

--
-- Name: remembermetoken; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE remembermetoken (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    token character varying(255),
    username character varying(255)
);


ALTER TABLE public.remembermetoken OWNER TO postgres;

--
-- Name: remotelink; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE remotelink (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    globalid character varying(255),
    title character varying(255),
    summary text,
    url text,
    iconurl text,
    icontitle text,
    relationship character varying(255),
    resolved character(1),
    statusname character varying(255),
    statusdescription text,
    statusiconurl text,
    statusicontitle text,
    statusiconlink text,
    statuscategorykey character varying(255),
    statuscategorycolorname character varying(255),
    applicationtype character varying(255),
    applicationname character varying(255)
);


ALTER TABLE public.remotelink OWNER TO postgres;

--
-- Name: replicatedindexoperation; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE replicatedindexoperation (
    id numeric(18,0) NOT NULL,
    index_time timestamp with time zone,
    node_id character varying(60),
    affected_index character varying(60),
    entity_type character varying(60),
    affected_ids text,
    operation character varying(60)
);


ALTER TABLE public.replicatedindexoperation OWNER TO postgres;

--
-- Name: resolution; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE resolution (
    id character varying(60) NOT NULL,
    sequence numeric(18,0),
    pname character varying(60),
    description text,
    iconurl character varying(255)
);


ALTER TABLE public.resolution OWNER TO postgres;

--
-- Name: rundetails; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rundetails (
    id numeric(18,0) NOT NULL,
    job_id character varying(255),
    start_time timestamp with time zone,
    run_duration numeric(18,0),
    run_outcome character(1),
    info_message character varying(255)
);


ALTER TABLE public.rundetails OWNER TO postgres;

--
-- Name: schemeissuesecurities; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schemeissuesecurities (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    security numeric(18,0),
    sec_type character varying(255),
    sec_parameter character varying(255)
);


ALTER TABLE public.schemeissuesecurities OWNER TO postgres;

--
-- Name: schemeissuesecuritylevels; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schemeissuesecuritylevels (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text,
    scheme numeric(18,0)
);


ALTER TABLE public.schemeissuesecuritylevels OWNER TO postgres;

--
-- Name: schemepermissions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schemepermissions (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    permission numeric(18,0),
    perm_type character varying(255),
    perm_parameter character varying(255),
    permission_key character varying(255)
);


ALTER TABLE public.schemepermissions OWNER TO postgres;

--
-- Name: searchrequest; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE searchrequest (
    id numeric(18,0) NOT NULL,
    filtername character varying(255),
    authorname character varying(255),
    description text,
    username character varying(255),
    groupname character varying(255),
    projectid numeric(18,0),
    reqcontent text,
    fav_count numeric(18,0),
    filtername_lower character varying(255)
);


ALTER TABLE public.searchrequest OWNER TO postgres;

--
-- Name: sequence_value_item; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sequence_value_item (
    seq_name character varying(60) NOT NULL,
    seq_id numeric(18,0)
);


ALTER TABLE public.sequence_value_item OWNER TO postgres;

--
-- Name: serviceconfig; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE serviceconfig (
    id numeric(18,0) NOT NULL,
    delaytime numeric(18,0),
    clazz character varying(255),
    servicename character varying(255)
);


ALTER TABLE public.serviceconfig OWNER TO postgres;

--
-- Name: sharepermissions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sharepermissions (
    id numeric(18,0) NOT NULL,
    entityid numeric(18,0),
    entitytype character varying(60),
    sharetype character varying(10),
    param1 character varying(255),
    param2 character varying(60)
);


ALTER TABLE public.sharepermissions OWNER TO postgres;

--
-- Name: trackback_ping; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE trackback_ping (
    id numeric(18,0) NOT NULL,
    issue numeric(18,0),
    url character varying(255),
    title character varying(255),
    blogname character varying(255),
    excerpt character varying(255),
    created timestamp with time zone
);


ALTER TABLE public.trackback_ping OWNER TO postgres;

--
-- Name: trustedapp; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE trustedapp (
    id numeric(18,0) NOT NULL,
    application_id character varying(255),
    name character varying(255),
    public_key text,
    ip_match text,
    url_match text,
    timeout numeric(18,0),
    created timestamp with time zone,
    created_by character varying(255),
    updated timestamp with time zone,
    updated_by character varying(255)
);


ALTER TABLE public.trustedapp OWNER TO postgres;

--
-- Name: upgradehistory; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE upgradehistory (
    id numeric(18,0),
    upgradeclass character varying(255) NOT NULL,
    targetbuild character varying(255)
);


ALTER TABLE public.upgradehistory OWNER TO postgres;

--
-- Name: upgradeversionhistory; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE upgradeversionhistory (
    id numeric(18,0),
    timeperformed timestamp with time zone,
    targetbuild character varying(255) NOT NULL,
    targetversion character varying(255)
);


ALTER TABLE public.upgradeversionhistory OWNER TO postgres;

--
-- Name: userassociation; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userassociation (
    source_name character varying(60) NOT NULL,
    sink_node_id numeric(18,0) NOT NULL,
    sink_node_entity character varying(60) NOT NULL,
    association_type character varying(60) NOT NULL,
    sequence numeric(9,0),
    created timestamp with time zone
);


ALTER TABLE public.userassociation OWNER TO postgres;

--
-- Name: userbase; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userbase (
    id numeric(18,0) NOT NULL,
    username character varying(255),
    password_hash character varying(255)
);


ALTER TABLE public.userbase OWNER TO postgres;

--
-- Name: userhistoryitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userhistoryitem (
    id numeric(18,0) NOT NULL,
    entitytype character varying(10),
    entityid character varying(60),
    username character varying(255),
    lastviewed numeric(18,0),
    data text
);


ALTER TABLE public.userhistoryitem OWNER TO postgres;

--
-- Name: userpickerfilter; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userpickerfilter (
    id numeric(18,0) NOT NULL,
    customfield numeric(18,0),
    customfieldconfig numeric(18,0),
    enabled character varying(60)
);


ALTER TABLE public.userpickerfilter OWNER TO postgres;

--
-- Name: userpickerfiltergroup; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userpickerfiltergroup (
    id numeric(18,0) NOT NULL,
    userpickerfilter numeric(18,0),
    groupname character varying(255)
);


ALTER TABLE public.userpickerfiltergroup OWNER TO postgres;

--
-- Name: userpickerfilterrole; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userpickerfilterrole (
    id numeric(18,0) NOT NULL,
    userpickerfilter numeric(18,0),
    projectroleid numeric(18,0)
);


ALTER TABLE public.userpickerfilterrole OWNER TO postgres;

--
-- Name: versioncontrol; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE versioncontrol (
    id numeric(18,0) NOT NULL,
    vcsname character varying(255),
    vcsdescription character varying(255),
    vcstype character varying(255)
);


ALTER TABLE public.versioncontrol OWNER TO postgres;

--
-- Name: votehistory; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE votehistory (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    votes numeric(18,0),
    "timestamp" timestamp with time zone
);


ALTER TABLE public.votehistory OWNER TO postgres;

--
-- Name: workflowscheme; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE workflowscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


ALTER TABLE public.workflowscheme OWNER TO postgres;

--
-- Name: workflowschemeentity; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE workflowschemeentity (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    workflow character varying(255),
    issuetype character varying(255)
);


ALTER TABLE public.workflowschemeentity OWNER TO postgres;

--
-- Name: worklog; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE worklog (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    author character varying(255),
    grouplevel character varying(255),
    rolelevel numeric(18,0),
    worklogbody text,
    created timestamp with time zone,
    updateauthor character varying(255),
    updated timestamp with time zone,
    startdate timestamp with time zone,
    timeworked numeric(18,0)
);


ALTER TABLE public.worklog OWNER TO postgres;

--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_21D670_WHITELIST_RULES" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_21D670_WHITELIST_RULES_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_4AEACD_WEBHOOK_DAO" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_4AEACD_WEBHOOK_DAO_ID_seq"'::regclass);


--
-- Name: ACTIVITY_ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_ACTIVITY_ENTITY" ALTER COLUMN "ACTIVITY_ID" SET DEFAULT nextval('"AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_ACTOR_ENTITY" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_563AEE_ACTOR_ENTITY_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_MEDIA_LINK_ENTITY" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_563AEE_MEDIA_LINK_ENTITY_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_OBJECT_ENTITY" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_563AEE_OBJECT_ENTITY_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_TARGET_ENTITY" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_563AEE_TARGET_ENTITY_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_B9A0F0_APPLIED_TEMPLATE" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_B9A0F0_APPLIED_TEMPLATE_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_BRANCH" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_BRANCH_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_BRANCH_HEAD_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_CHANGESET_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_CHANGESET_MAPPING_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_COMMIT" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_COMMIT_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_GIT_HUB_EVENT" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_GIT_HUB_EVENT_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_ISSUE_MAPPING_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_MAPPING_V2" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_TO_BRANCH" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_TO_CHANGESET" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_MESSAGE" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_MESSAGE_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_MESSAGE_QUEUE_ITEM" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_MESSAGE_TAG" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_MESSAGE_TAG_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_ORGANIZATION_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PROJECT_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_PROJECT_MAPPING_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PROJECT_MAPPING_V2" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PR_ISSUE_KEY" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_PR_ISSUE_KEY_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PR_PARTICIPANT" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_PR_PARTICIPANT_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PR_TO_COMMIT" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_PR_TO_COMMIT_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PULL_REQUEST" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_PULL_REQUEST_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_REPOSITORY_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_REPOSITORY_MAPPING_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_REPO_TO_CHANGESET" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_REPO_TO_CHANGESET_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_SYNC_AUDIT_LOG" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq"'::regclass);


--
-- Name: ID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_SYNC_EVENT" ALTER COLUMN "ID" SET DEFAULT nextval('"AO_E8B6CC_SYNC_EVENT_ID_seq"'::regclass);


--
-- Data for Name: AO_21D670_WHITELIST_RULES; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "AO_21D670_WHITELIST_RULES" VALUES (false, 'http://www.atlassian.com/*', 1, 'WILDCARD_EXPRESSION');


--
-- Name: AO_21D670_WHITELIST_RULES_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_21D670_WHITELIST_RULES_ID_seq"', 1, true);


--
-- Data for Name: AO_4AEACD_WEBHOOK_DAO; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_4AEACD_WEBHOOK_DAO_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_4AEACD_WEBHOOK_DAO_ID_seq"', 1, false);


--
-- Data for Name: AO_563AEE_ACTIVITY_ENTITY; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq"', 1, false);


--
-- Data for Name: AO_563AEE_ACTOR_ENTITY; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_563AEE_ACTOR_ENTITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_563AEE_ACTOR_ENTITY_ID_seq"', 1, false);


--
-- Data for Name: AO_563AEE_MEDIA_LINK_ENTITY; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_563AEE_MEDIA_LINK_ENTITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_563AEE_MEDIA_LINK_ENTITY_ID_seq"', 1, false);


--
-- Data for Name: AO_563AEE_OBJECT_ENTITY; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_563AEE_OBJECT_ENTITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_563AEE_OBJECT_ENTITY_ID_seq"', 1, false);


--
-- Data for Name: AO_563AEE_TARGET_ENTITY; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_563AEE_TARGET_ENTITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_563AEE_TARGET_ENTITY_ID_seq"', 1, false);


--
-- Data for Name: AO_B9A0F0_APPLIED_TEMPLATE; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_B9A0F0_APPLIED_TEMPLATE_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_B9A0F0_APPLIED_TEMPLATE_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_BRANCH; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: AO_E8B6CC_BRANCH_HEAD_MAPPING; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq"', 1, false);


--
-- Name: AO_E8B6CC_BRANCH_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_BRANCH_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_CHANGESET_MAPPING; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_CHANGESET_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_CHANGESET_MAPPING_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_COMMIT; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_COMMIT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_COMMIT_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_GIT_HUB_EVENT; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_GIT_HUB_EVENT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_GIT_HUB_EVENT_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_ISSUE_MAPPING; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_ISSUE_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_ISSUE_MAPPING_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_ISSUE_MAPPING_V2; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_ISSUE_TO_BRANCH; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_ISSUE_TO_CHANGESET; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_MESSAGE; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_MESSAGE_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_MESSAGE_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_MESSAGE_TAG; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_MESSAGE_TAG_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_MESSAGE_TAG_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_ORGANIZATION_MAPPING; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_PROJECT_MAPPING; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_PROJECT_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_PROJECT_MAPPING_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_PROJECT_MAPPING_V2; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_PR_ISSUE_KEY; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_PR_ISSUE_KEY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_PR_ISSUE_KEY_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_PR_PARTICIPANT; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_PR_PARTICIPANT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_PR_PARTICIPANT_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_PR_TO_COMMIT; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_PR_TO_COMMIT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_PR_TO_COMMIT_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_PULL_REQUEST; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_PULL_REQUEST_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_PULL_REQUEST_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_REPOSITORY_MAPPING; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_REPOSITORY_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_REPOSITORY_MAPPING_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_REPO_TO_CHANGESET; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_REPO_TO_CHANGESET_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_REPO_TO_CHANGESET_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_SYNC_AUDIT_LOG; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq"', 1, false);


--
-- Data for Name: AO_E8B6CC_SYNC_EVENT; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_E8B6CC_SYNC_EVENT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"AO_E8B6CC_SYNC_EVENT_ID_seq"', 1, false);


--
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO app_user VALUES (10000, 'admin', 'admin');


--
-- Data for Name: audit_changed_value; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO audit_changed_value VALUES (10000, 10000, 'Username', NULL, 'admin');
INSERT INTO audit_changed_value VALUES (10001, 10000, 'Full Name', NULL, 'Administrator');
INSERT INTO audit_changed_value VALUES (10002, 10000, 'Email', NULL, 'admin@example.com');
INSERT INTO audit_changed_value VALUES (10003, 10000, 'Active / Inactive', NULL, 'Active');


--
-- Data for Name: audit_item; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO audit_item VALUES (10000, 10001, 'USER', 'admin', 'admin', '1', 'JIRA Internal Directory');
INSERT INTO audit_item VALUES (10001, 10002, 'USER', 'admin', 'admin', '1', 'JIRA Internal Directory');
INSERT INTO audit_item VALUES (10002, 10003, 'USER', 'admin', 'admin', '1', 'JIRA Internal Directory');


--
-- Data for Name: audit_log; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO audit_log VALUES (10000, '0:0:0:0:0:0:0:1', '2014-11-14 03:24:05.38+07', NULL, 'User created', 'user management', 'USER', 'admin', 'admin', '1', 'JIRA Internal Directory', 0, '', '0:0:0:0:0:0:0:1 user created management admin jira internal directory administrator admin@example.com active');
INSERT INTO audit_log VALUES (10001, '0:0:0:0:0:0:0:1', '2014-11-14 03:24:05.456+07', NULL, 'User added to group', 'group management', 'GROUP', NULL, 'jira-users', '1', 'JIRA Internal Directory', 0, '', '0:0:0:0:0:0:0:1 user added to group management jira-users jira internal directory admin');
INSERT INTO audit_log VALUES (10002, '0:0:0:0:0:0:0:1', '2014-11-14 03:24:05.521+07', NULL, 'User added to group', 'group management', 'GROUP', NULL, 'jira-administrators', '1', 'JIRA Internal Directory', 0, '', '0:0:0:0:0:0:0:1 user added to group management jira-administrators jira internal directory admin');
INSERT INTO audit_log VALUES (10003, '0:0:0:0:0:0:0:1', '2014-11-14 03:24:05.537+07', NULL, 'User added to group', 'group management', 'GROUP', NULL, 'jira-developers', '1', 'JIRA Internal Directory', 0, '', '0:0:0:0:0:0:0:1 user added to group management jira-developers jira internal directory admin');


--
-- Data for Name: avatar; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO avatar VALUES (10000, 'codegeist.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10001, 'eamesbird.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10002, 'jm_black.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10003, 'jm_brown.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10004, 'jm_orange.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10005, 'jm_red.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10006, 'jm_white.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10007, 'jm_yellow.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10008, 'monster.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10009, 'rainbow.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10010, 'kangaroo.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10011, 'rocket.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10100, 'Avatar-1.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10101, 'Avatar-2.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10102, 'Avatar-3.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10103, 'Avatar-4.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10104, 'Avatar-5.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10105, 'Avatar-6.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10106, 'Avatar-7.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10107, 'Avatar-8.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10108, 'Avatar-9.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10109, 'Avatar-10.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10110, 'Avatar-11.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10111, 'Avatar-12.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10112, 'Avatar-13.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10113, 'Avatar-14.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10114, 'Avatar-15.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10115, 'Avatar-16.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10116, 'Avatar-17.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10117, 'Avatar-18.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10118, 'Avatar-19.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10119, 'Avatar-20.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10120, 'Avatar-21.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10121, 'Avatar-22.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10122, 'Avatar-default.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10123, 'Avatar-unknown.png', 'image/png', 'user', NULL, 1);
INSERT INTO avatar VALUES (10200, 'cloud.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10201, 'config.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10202, 'disc.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10203, 'finance.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10204, 'hand.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10205, 'new_monster.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10206, 'power.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10207, 'refresh.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10208, 'servicedesk.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10209, 'settings.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10210, 'storm.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10211, 'travel.png', 'image/png', 'project', NULL, 1);
INSERT INTO avatar VALUES (10300, 'genericissue.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10301, 'all_unassigned.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10302, 'blank.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10303, 'bug.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10304, 'defect.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10305, 'delete.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10306, 'documentation.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10307, 'epic.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10308, 'exclamation.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10309, 'health.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10310, 'improvement.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10311, 'newfeature.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10312, 'remove_feature.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10313, 'requirement.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10314, 'sales.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10315, 'story.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10316, 'subtask.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10317, 'subtask_alternate.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10318, 'task.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10319, 'task_agile.png', 'image/png', 'issuetype', NULL, 1);
INSERT INTO avatar VALUES (10320, 'undefined.png', 'image/png', 'issuetype', NULL, 1);


--
-- Data for Name: changegroup; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: changeitem; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: clusterlockstatus; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: clustermessage; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: clusternode; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: clusternodeheartbeat; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: columnlayout; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: columnlayoutitem; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: component; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: configurationcontext; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO configurationcontext VALUES (10000, NULL, NULL, 'issuetype', 10000);


--
-- Data for Name: customfield; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: customfieldoption; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: customfieldvalue; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: cwd_application; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO cwd_application VALUES (1, 'crowd-embedded', 'crowd-embedded', '2013-02-28 07:57:51.302+07', '2013-02-28 07:57:51.302+07', 1, '', 'CROWD', 'X');


--
-- Data for Name: cwd_application_address; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: cwd_directory; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO cwd_directory VALUES (1, 'JIRA Internal Directory', 'jira internal directory', '2013-02-28 07:57:51.308+07', '2013-02-28 07:57:51.308+07', 1, 'JIRA default internal directory', 'com.atlassian.crowd.directory.InternalDirectory', 'com.atlassian.crowd.directory.internaldirectory', 'INTERNAL', 0);


--
-- Data for Name: cwd_directory_attribute; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO cwd_directory_attribute VALUES (1, 'user_encryption_method', 'atlassian-security');


--
-- Data for Name: cwd_directory_operation; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO cwd_directory_operation VALUES (1, 'CREATE_GROUP');
INSERT INTO cwd_directory_operation VALUES (1, 'CREATE_ROLE');
INSERT INTO cwd_directory_operation VALUES (1, 'CREATE_USER');
INSERT INTO cwd_directory_operation VALUES (1, 'DELETE_GROUP');
INSERT INTO cwd_directory_operation VALUES (1, 'DELETE_ROLE');
INSERT INTO cwd_directory_operation VALUES (1, 'DELETE_USER');
INSERT INTO cwd_directory_operation VALUES (1, 'UPDATE_GROUP');
INSERT INTO cwd_directory_operation VALUES (1, 'UPDATE_GROUP_ATTRIBUTE');
INSERT INTO cwd_directory_operation VALUES (1, 'UPDATE_ROLE');
INSERT INTO cwd_directory_operation VALUES (1, 'UPDATE_ROLE_ATTRIBUTE');
INSERT INTO cwd_directory_operation VALUES (1, 'UPDATE_USER');
INSERT INTO cwd_directory_operation VALUES (1, 'UPDATE_USER_ATTRIBUTE');


--
-- Data for Name: cwd_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO cwd_group VALUES (10000, 'jira-administrators', 'jira-administrators', 1, 0, '2013-02-28 07:57:51.326+07', '2013-02-28 07:57:51.326+07', '', NULL, 'GROUP', 1);
INSERT INTO cwd_group VALUES (10001, 'jira-developers', 'jira-developers', 1, 0, '2013-02-28 07:57:51.326+07', '2013-02-28 07:57:51.326+07', '', NULL, 'GROUP', 1);
INSERT INTO cwd_group VALUES (10002, 'jira-users', 'jira-users', 1, 0, '2013-02-28 07:57:51.326+07', '2013-02-28 07:57:51.326+07', '', NULL, 'GROUP', 1);


--
-- Data for Name: cwd_group_attributes; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: cwd_membership; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO cwd_membership VALUES (10000, 10002, 10000, 'GROUP_USER', NULL, 'jira-users', 'jira-users', 'admin', 'admin', 1);
INSERT INTO cwd_membership VALUES (10001, 10000, 10000, 'GROUP_USER', NULL, 'jira-administrators', 'jira-administrators', 'admin', 'admin', 1);
INSERT INTO cwd_membership VALUES (10002, 10001, 10000, 'GROUP_USER', NULL, 'jira-developers', 'jira-developers', 'admin', 'admin', 1);


--
-- Data for Name: cwd_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO cwd_user VALUES (10000, 1, 'admin', 'admin', 1, '2014-11-14 03:24:05.262+07', '2014-11-14 03:24:05.262+07', '', '', 'Administrator', 'administrator', 'Administrator', 'administrator', 'admin@example.com', 'admin@example.com', '{PKCS5S2}q8zhuvNBdMoX9LyjqUDpZcLowc65CFf6Ow3CEvAL52xXfjTremcq/5250uNOHhCs', NULL, '121ebcf1-fef4-4e41-992b-8a8ccaa9b40e');


--
-- Data for Name: cwd_user_attributes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO cwd_user_attributes VALUES (10000, 10000, 1, 'requiresPasswordChange', 'false', 'false');
INSERT INTO cwd_user_attributes VALUES (10001, 10000, 1, 'passwordLastChanged', '1415910245240', '1415910245240');
INSERT INTO cwd_user_attributes VALUES (10002, 10000, 1, 'password.reset.request.expiry', '1415996645488', '1415996645488');
INSERT INTO cwd_user_attributes VALUES (10003, 10000, 1, 'password.reset.request.token', '896c9167e0d650e9b387078447ecb9a3e11f42ec', '896c9167e0d650e9b387078447ecb9a3e11f42ec');


--
-- Data for Name: draftworkflowscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: draftworkflowschemeentity; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: entity_property; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: entity_property_index_document; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: external_entities; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: externalgadget; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: favouriteassociations; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: feature; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: fieldconfigscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldconfigscheme VALUES (10000, 'Default Issue Type Scheme', 'Default issue type scheme is the list of global issue types. All newly created issue types will automatically be added to this scheme.', 'issuetype', NULL);


--
-- Data for Name: fieldconfigschemeissuetype; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldconfigschemeissuetype VALUES (10100, NULL, 10000, 10000);


--
-- Data for Name: fieldconfiguration; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldconfiguration VALUES (10000, 'Default Configuration for Issue Type', 'Default configuration generated by JIRA', 'issuetype', NULL);


--
-- Data for Name: fieldlayout; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldlayout VALUES (10000, 'Default Field Configuration', 'The default field configuration', 'default', NULL);


--
-- Data for Name: fieldlayoutitem; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldlayoutitem VALUES (10100, 10000, 'summary', NULL, NULL, 'false', 'true', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10101, 10000, 'issuetype', NULL, NULL, 'false', 'true', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10102, 10000, 'security', NULL, NULL, 'false', 'false', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10103, 10000, 'priority', NULL, NULL, 'false', 'false', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10104, 10000, 'duedate', NULL, NULL, 'false', 'false', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10105, 10000, 'components', NULL, NULL, 'false', 'false', 'frother-control-renderer');
INSERT INTO fieldlayoutitem VALUES (10106, 10000, 'versions', NULL, NULL, 'false', 'false', 'frother-control-renderer');
INSERT INTO fieldlayoutitem VALUES (10107, 10000, 'fixVersions', NULL, NULL, 'false', 'false', 'frother-control-renderer');
INSERT INTO fieldlayoutitem VALUES (10108, 10000, 'assignee', NULL, NULL, 'false', 'false', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10109, 10000, 'reporter', NULL, NULL, 'false', 'true', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10110, 10000, 'environment', 'For example operating system, software platform and/or hardware specifications (include as appropriate for the issue).', NULL, 'false', 'false', 'atlassian-wiki-renderer');
INSERT INTO fieldlayoutitem VALUES (10111, 10000, 'description', NULL, NULL, 'false', 'false', 'atlassian-wiki-renderer');
INSERT INTO fieldlayoutitem VALUES (10112, 10000, 'timetracking', 'An estimate of how much work remains until this issue will be resolved.<br>The format of this is '' *w *d *h *m '' (representing weeks, days, hours and minutes - where * can be any number)<br>Examples: 4d, 5h 30m, 60m and 3w.<br>', NULL, 'false', 'false', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10113, 10000, 'resolution', NULL, NULL, 'false', 'false', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10114, 10000, 'attachment', NULL, NULL, 'false', 'false', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10115, 10000, 'comment', NULL, NULL, 'false', 'false', 'atlassian-wiki-renderer');
INSERT INTO fieldlayoutitem VALUES (10116, 10000, 'labels', NULL, NULL, 'false', 'false', 'jira-text-renderer');
INSERT INTO fieldlayoutitem VALUES (10117, 10000, 'worklog', 'Allows work to be logged whilst creating, editing or transitioning issues.', NULL, 'false', 'false', 'atlassian-wiki-renderer');
INSERT INTO fieldlayoutitem VALUES (10118, 10000, 'issuelinks', NULL, NULL, 'false', 'false', 'jira-text-renderer');


--
-- Data for Name: fieldlayoutscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: fieldlayoutschemeassociation; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: fieldlayoutschemeentity; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: fieldscreen; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldscreen VALUES (1, 'Default Screen', 'Allows to update all system fields.');
INSERT INTO fieldscreen VALUES (2, 'Workflow Screen', 'This screen is used in the workflow and enables you to assign issues');
INSERT INTO fieldscreen VALUES (3, 'Resolve Issue Screen', 'Allows to set resolution, change fix versions and assign an issue.');


--
-- Data for Name: fieldscreenlayoutitem; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldscreenlayoutitem VALUES (10000, 'summary', 0, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10001, 'issuetype', 1, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10002, 'security', 2, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10003, 'priority', 3, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10004, 'duedate', 4, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10005, 'components', 5, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10006, 'versions', 6, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10007, 'fixVersions', 7, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10008, 'assignee', 8, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10009, 'reporter', 9, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10010, 'environment', 10, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10011, 'description', 11, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10012, 'timetracking', 12, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10013, 'attachment', 13, 10000);
INSERT INTO fieldscreenlayoutitem VALUES (10014, 'assignee', 0, 10001);
INSERT INTO fieldscreenlayoutitem VALUES (10015, 'resolution', 0, 10002);
INSERT INTO fieldscreenlayoutitem VALUES (10016, 'fixVersions', 1, 10002);
INSERT INTO fieldscreenlayoutitem VALUES (10017, 'assignee', 2, 10002);
INSERT INTO fieldscreenlayoutitem VALUES (10018, 'worklog', 3, 10002);
INSERT INTO fieldscreenlayoutitem VALUES (10100, 'labels', 14, 10000);


--
-- Data for Name: fieldscreenscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldscreenscheme VALUES (1, 'Default Screen Scheme', 'Default Screen Scheme');


--
-- Data for Name: fieldscreenschemeitem; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldscreenschemeitem VALUES (10000, NULL, 1, 1);


--
-- Data for Name: fieldscreentab; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fieldscreentab VALUES (10002, 'Field Tab', NULL, 0, 3);
INSERT INTO fieldscreentab VALUES (10000, 'Field Tab', NULL, 0, 1);
INSERT INTO fieldscreentab VALUES (10001, 'Field Tab', NULL, 0, 2);


--
-- Data for Name: fileattachment; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: filtersubscription; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: gadgetuserpreference; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO gadgetuserpreference VALUES (10000, 10002, 'isConfigured', 'true');
INSERT INTO gadgetuserpreference VALUES (10001, 10003, 'keys', '__all_projects__');
INSERT INTO gadgetuserpreference VALUES (10002, 10003, 'isConfigured', 'true');
INSERT INTO gadgetuserpreference VALUES (10003, 10003, 'title', 'Your Company JIRA');
INSERT INTO gadgetuserpreference VALUES (10004, 10003, 'numofentries', '5');


--
-- Data for Name: genericconfiguration; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO genericconfiguration VALUES (10000, 'DefaultValue', '10000', '<string>1</string>');


--
-- Data for Name: globalpermissionentry; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO globalpermissionentry VALUES (10000, 'ADMINISTER', 'jira-administrators');
INSERT INTO globalpermissionentry VALUES (10001, 'USE', 'jira-users');
INSERT INTO globalpermissionentry VALUES (10002, 'USER_PICKER', 'jira-developers');
INSERT INTO globalpermissionentry VALUES (10003, 'MANAGE_GROUP_FILTER_SUBSCRIPTIONS', 'jira-developers');
INSERT INTO globalpermissionentry VALUES (10004, 'CREATE_SHARED_OBJECTS', 'jira-users');
INSERT INTO globalpermissionentry VALUES (10005, 'BULK_CHANGE', 'jira-users');
INSERT INTO globalpermissionentry VALUES (10006, 'SYSTEM_ADMIN', 'jira-administrators');


--
-- Data for Name: groupbase; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: issuelink; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: issuelinktype; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO issuelinktype VALUES (10000, 'Blocks', 'is blocked by', 'blocks', NULL);
INSERT INTO issuelinktype VALUES (10001, 'Cloners', 'is cloned by', 'clones', NULL);
INSERT INTO issuelinktype VALUES (10002, 'Duplicate', 'is duplicated by', 'duplicates', NULL);
INSERT INTO issuelinktype VALUES (10003, 'Relates', 'relates to', 'relates to', NULL);
INSERT INTO issuelinktype VALUES (10100, 'jira_subtask_link', 'jira_subtask_inward', 'jira_subtask_outward', 'jira_subtask');


--
-- Data for Name: issuesecurityscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: issuestatus; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO issuestatus VALUES ('1', 1, 'Open', 'The issue is open and ready for the assignee to start work on it.', '/images/icons/statuses/open.png', 2);
INSERT INTO issuestatus VALUES ('3', 3, 'In Progress', 'This issue is being actively worked on at the moment by the assignee.', '/images/icons/statuses/inprogress.png', 4);
INSERT INTO issuestatus VALUES ('4', 4, 'Reopened', 'This issue was once resolved, but the resolution was deemed incorrect. From here issues are either marked assigned or resolved.', '/images/icons/statuses/reopened.png', 2);
INSERT INTO issuestatus VALUES ('5', 5, 'Resolved', 'A resolution has been taken, and it is awaiting verification by reporter. From here issues are either reopened, or are closed.', '/images/icons/statuses/resolved.png', 3);
INSERT INTO issuestatus VALUES ('6', 6, 'Closed', 'The issue is considered finished, the resolution is correct. Issues which are closed can be reopened.', '/images/icons/statuses/closed.png', 3);


--
-- Data for Name: issuetype; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO issuetype VALUES ('1', 1, 'Bug', NULL, 'A problem which impairs or prevents the functions of the product.', '/images/icons/issuetypes/bug.png', NULL);
INSERT INTO issuetype VALUES ('2', 2, 'New Feature', NULL, 'A new feature of the product, which has yet to be developed.', '/images/icons/issuetypes/newfeature.png', NULL);
INSERT INTO issuetype VALUES ('3', 3, 'Task', NULL, 'A task that needs to be done.', '/images/icons/issuetypes/task.png', NULL);
INSERT INTO issuetype VALUES ('4', 4, 'Improvement', NULL, 'An improvement or enhancement to an existing feature or task.', '/images/icons/issuetypes/improvement.png', NULL);
INSERT INTO issuetype VALUES ('5', 0, 'Sub-task', 'jira_subtask', 'The sub-task of the issue', '/images/icons/issuetypes/subtask_alternate.png', NULL);


--
-- Data for Name: issuetypescreenscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO issuetypescreenscheme VALUES (1, 'Default Issue Type Screen Scheme', 'The default issue type screen scheme');


--
-- Data for Name: issuetypescreenschemeentity; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO issuetypescreenschemeentity VALUES (10000, NULL, 1, 1);


--
-- Data for Name: jiraaction; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jiradraftworkflows; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jiraeventtype; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO jiraeventtype VALUES (2, NULL, 'Issue Updated', 'This is the ''issue updated'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (3, NULL, 'Issue Assigned', 'This is the ''issue assigned'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (4, NULL, 'Issue Resolved', 'This is the ''issue resolved'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (5, NULL, 'Issue Closed', 'This is the ''issue closed'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (6, NULL, 'Issue Commented', 'This is the ''issue commented'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (7, NULL, 'Issue Reopened', 'This is the ''issue reopened'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (8, NULL, 'Issue Deleted', 'This is the ''issue deleted'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (9, NULL, 'Issue Moved', 'This is the ''issue moved'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (10, NULL, 'Work Logged On Issue', 'This is the ''work logged on issue'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (11, NULL, 'Work Started On Issue', 'This is the ''work started on issue'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (12, NULL, 'Work Stopped On Issue', 'This is the ''work stopped on issue'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (13, NULL, 'Generic Event', 'This is the ''generic event'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (14, NULL, 'Issue Comment Edited', 'This is the ''issue comment edited'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (15, NULL, 'Issue Worklog Updated', 'This is the ''issue worklog updated'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (16, NULL, 'Issue Worklog Deleted', 'This is the ''issue worklog deleted'' event.', 'jira.system.event.type');
INSERT INTO jiraeventtype VALUES (1, NULL, 'Issue Created', 'This is the ''issue created'' event.', 'jira.system.event.type');


--
-- Data for Name: jiraissue; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jiraperms; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jiraworkflows; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO jiraworkflows VALUES (10000, 'classic default workflow', NULL, '<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE workflow PUBLIC "-//OpenSymphony Group//DTD OSWorkflow 2.8//EN" "http://www.opensymphony.com/osworkflow/workflow_2_8.dtd">
<workflow>
  <meta name="jira.description">The classic JIRA default workflow</meta>
  <initial-actions>
    <action id="1" name="Create Issue">
      <meta name="opsbar-sequence">0</meta>
      <meta name="jira.i18n.title">common.forms.create</meta>
      <validators>
        <validator name="" type="class">
          <arg name="class.name">com.atlassian.jira.workflow.validator.PermissionValidator</arg>
          <arg name="permission">Create Issue</arg>
        </validator>
      </validators>
      <results>
        <unconditional-result old-status="Finished" status="Open" step="1">
          <post-functions>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueCreateFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>
              <arg name="eventTypeId">1</arg>
            </function>
          </post-functions>
        </unconditional-result>
      </results>
    </action>
  </initial-actions>
  <common-actions>
    <action id="2" name="Close Issue" view="resolveissue">
      <meta name="opsbar-sequence">60</meta>
      <meta name="jira.i18n.submit">closeissue.close</meta>
      <meta name="jira.i18n.description">closeissue.desc</meta>
      <meta name="jira.i18n.title">closeissue.title</meta>
      <restrict-to>
        <conditions type="AND">
          <condition type="class">
            <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>
            <arg name="permission">Resolve Issue</arg>
          </condition>
          <condition type="class">
            <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>
            <arg name="permission">Close Issue</arg>
          </condition>
        </conditions>
      </restrict-to>
      <results>
        <unconditional-result old-status="Finished" status="Closed" step="6">
          <post-functions>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>
              <arg name="eventTypeId">5</arg>
            </function>
          </post-functions>
        </unconditional-result>
      </results>
    </action>
    <action id="3" name="Reopen Issue" view="commentassign">
      <meta name="opsbar-sequence">80</meta>
      <meta name="jira.i18n.submit">issue.operations.reopen.issue</meta>
      <meta name="jira.i18n.description">issue.operations.reopen.description</meta>
      <meta name="jira.i18n.title">issue.operations.reopen.issue</meta>
      <restrict-to>
        <conditions>
          <condition type="class">
            <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>
            <arg name="permission">Resolve Issue</arg>
          </condition>
        </conditions>
      </restrict-to>
      <results>
        <unconditional-result old-status="Finished" status="Reopened" step="5">
          <post-functions>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction</arg>
              <arg name="field.value"></arg>
              <arg name="field.name">resolution</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>
              <arg name="eventTypeId">7</arg>
            </function>
          </post-functions>
        </unconditional-result>
      </results>
    </action>
    <action id="4" name="Start Progress">
      <meta name="opsbar-sequence">20</meta>
      <meta name="jira.i18n.title">startprogress.title</meta>
      <restrict-to>
        <conditions>
          <condition type="class">
            <arg name="class.name">com.atlassian.jira.workflow.condition.AllowOnlyAssignee</arg>
          </condition>
        </conditions>
      </restrict-to>
      <results>
        <unconditional-result old-status="Finished" status="Underway" step="3">
          <post-functions>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction</arg>
              <arg name="field.value"></arg>
              <arg name="field.name">resolution</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>
              <arg name="eventTypeId">11</arg>
            </function>
          </post-functions>
        </unconditional-result>
      </results>
    </action>
    <action id="5" name="Resolve Issue" view="resolveissue">
      <meta name="opsbar-sequence">40</meta>
      <meta name="jira.i18n.submit">resolveissue.resolve</meta>
      <meta name="jira.i18n.description">resolveissue.desc.line1</meta>
      <meta name="jira.i18n.title">resolveissue.title</meta>
      <restrict-to>
        <conditions>
          <condition type="class">
            <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>
            <arg name="permission">Resolve Issue</arg>
          </condition>
        </conditions>
      </restrict-to>
      <results>
        <unconditional-result old-status="Finished" status="Resolved" step="4">
          <post-functions>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>
            </function>
            <function type="class">
              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>
              <arg name="eventTypeId">4</arg>
            </function>
          </post-functions>
        </unconditional-result>
      </results>
    </action>
  </common-actions>
  <steps>
    <step id="1" name="Open">
      <meta name="jira.status.id">1</meta>
      <actions>
<common-action id="4" />
<common-action id="5" />
<common-action id="2" />
      </actions>
    </step>
    <step id="3" name="In Progress">
      <meta name="jira.status.id">3</meta>
      <actions>
<common-action id="5" />
<common-action id="2" />
        <action id="301" name="Stop Progress">
          <meta name="opsbar-sequence">20</meta>
          <meta name="jira.i18n.title">stopprogress.title</meta>
          <restrict-to>
            <conditions>
              <condition type="class">
                <arg name="class.name">com.atlassian.jira.workflow.condition.AllowOnlyAssignee</arg>
              </condition>
            </conditions>
          </restrict-to>
          <results>
            <unconditional-result old-status="Finished" status="Assigned" step="1">
              <post-functions>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction</arg>
                  <arg name="field.value"></arg>
                  <arg name="field.name">resolution</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>
                  <arg name="eventTypeId">12</arg>
                </function>
              </post-functions>
            </unconditional-result>
          </results>
        </action>
      </actions>
    </step>
    <step id="4" name="Resolved">
      <meta name="jira.status.id">5</meta>
      <actions>
<common-action id="3" />
        <action id="701" name="Close Issue" view="commentassign">
          <meta name="opsbar-sequence">60</meta>
          <meta name="jira.i18n.submit">closeissue.close</meta>
          <meta name="jira.i18n.description">closeissue.desc</meta>
          <meta name="jira.i18n.title">closeissue.title</meta>
          <meta name="jira.description">Closing an issue indicates there is no more work to be done on it, and it has been verified as complete.</meta>
          <restrict-to>
            <conditions>
              <condition type="class">
                <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>
                <arg name="permission">Close Issue</arg>
              </condition>
            </conditions>
          </restrict-to>
          <results>
            <unconditional-result old-status="Finished" status="Closed" step="6">
              <post-functions>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>
                </function>
                <function type="class">
                  <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>
                  <arg name="eventTypeId">5</arg>
                </function>
              </post-functions>
            </unconditional-result>
          </results>
        </action>
      </actions>
    </step>
    <step id="5" name="Reopened">
      <meta name="jira.status.id">4</meta>
      <actions>
<common-action id="5" />
<common-action id="2" />
<common-action id="4" />
      </actions>
    </step>
    <step id="6" name="Closed">
      <meta name="jira.status.id">6</meta>
      <meta name="jira.issue.editable">false</meta>
      <actions>
<common-action id="3" />
      </actions>
    </step>
  </steps>
</workflow>
', NULL);


--
-- Data for Name: jquartz_blob_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_calendars; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_cron_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_fired_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_job_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO jquartz_job_details VALUES (NULL, 'com.atlassian.jira.service.DefaultServiceManager', 'SchedulerServiceJobs', NULL, 'com.atlassian.scheduler.quartz1.Quartz1Job', false, false, false, NULL, NULL, false, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787000737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c770800000010000000007800');
INSERT INTO jquartz_job_details VALUES (NULL, 'CompatibilityPluginScheduler.JobRunnerKey.LocalPluginLicenseNotificationJob-job-handler', 'SchedulerServiceJobs', NULL, 'com.atlassian.scheduler.quartz1.Quartz1Job', false, false, false, NULL, NULL, false, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787000737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c770800000010000000007800');
INSERT INTO jquartz_job_details VALUES (NULL, 'CompatibilityPluginScheduler.JobRunnerKey.RemotePluginLicenseNotificationJob-job-handler', 'SchedulerServiceJobs', NULL, 'com.atlassian.scheduler.quartz1.Quartz1Job', false, false, false, NULL, NULL, false, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787000737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c770800000010000000007800');
INSERT INTO jquartz_job_details VALUES (NULL, 'CompatibilityPluginScheduler.JobRunnerKey.PluginRequestCheckJob-job-handler', 'SchedulerServiceJobs', NULL, 'com.atlassian.scheduler.quartz1.Quartz1Job', false, false, false, NULL, NULL, false, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787000737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c770800000010000000007800');
INSERT INTO jquartz_job_details VALUES (NULL, 'CompatibilityPluginScheduler.JobRunnerKey.PluginUpdateCheckJob-job-handler', 'SchedulerServiceJobs', NULL, 'com.atlassian.scheduler.quartz1.Quartz1Job', false, false, false, NULL, NULL, false, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787000737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c770800000010000000007800');
INSERT INTO jquartz_job_details VALUES (NULL, 'CompatibilityPluginScheduler.JobRunnerKey.com.atlassian.jira.plugins.dvcs.scheduler.DvcsScheduler', 'SchedulerServiceJobs', NULL, 'com.atlassian.scheduler.quartz1.Quartz1Job', false, false, false, NULL, NULL, false, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787000737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c770800000010000000007800');


--
-- Data for Name: jquartz_job_listeners; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_locks; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO jquartz_locks VALUES (NULL, 'TRIGGER_ACCESS');
INSERT INTO jquartz_locks VALUES (NULL, 'JOB_ACCESS');
INSERT INTO jquartz_locks VALUES (NULL, 'CALENDAR_ACCESS');
INSERT INTO jquartz_locks VALUES (NULL, 'STATE_ACCESS');
INSERT INTO jquartz_locks VALUES (NULL, 'MISFIRE_ACCESS');


--
-- Data for Name: jquartz_paused_trigger_grps; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_scheduler_state; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_simple_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO jquartz_simple_triggers VALUES (NULL, 'com.atlassian.jira.service.JiraService:10002', 'SchedulerServiceTriggers', -1, 86400000, 1);
INSERT INTO jquartz_simple_triggers VALUES (NULL, 'com.atlassian.jira.service.JiraService:10001', 'SchedulerServiceTriggers', -1, 43200000, 0);
INSERT INTO jquartz_simple_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.LocalPluginLicenseNotificationJob-job', 'SchedulerServiceTriggers', -1, 86400000, 1);
INSERT INTO jquartz_simple_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.RemotePluginLicenseNotificationJob-job', 'SchedulerServiceTriggers', -1, 3600000, 1);
INSERT INTO jquartz_simple_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.PluginUpdateCheckJob-job', 'SchedulerServiceTriggers', -1, 86400000, 0);
INSERT INTO jquartz_simple_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.PluginRequestCheckJob-job', 'SchedulerServiceTriggers', -1, 3600000, 1);
INSERT INTO jquartz_simple_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.com.atlassian.jira.plugins.dvcs.scheduler.DvcsScheduler:job', 'SchedulerServiceTriggers', -1, 3600000, 1);


--
-- Data for Name: jquartz_simprop_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_trigger_listeners; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: jquartz_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO jquartz_triggers VALUES (NULL, 'com.atlassian.jira.service.JiraService:10002', 'SchedulerServiceTriggers', 'com.atlassian.jira.service.DefaultServiceManager', 'SchedulerServiceJobs', false, NULL, 1415996483063, 1415910083063, 5, 'WAITING', 'SIMPLE', 1415910083063, 0, NULL, 0, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787001737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c7708000000100000000174000a706172616d6574657273757200025b42acf317f8060854e002000078700000012baced000573720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00017870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c020000787000000001740033636f6d2e61746c61737369616e2e6a6972612e736572766963652e536572766963654d616e616765723a7365727669636549647571007e0003000000017372000e6a6176612e6c616e672e4c6f6e673b8be490cc8f23df0200014a000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b020000787000000000000027127800');
INSERT INTO jquartz_triggers VALUES (NULL, 'com.atlassian.jira.service.JiraService:10001', 'SchedulerServiceTriggers', 'com.atlassian.jira.service.DefaultServiceManager', 'SchedulerServiceJobs', false, NULL, 1415953283953, -1, 5, 'WAITING', 'SIMPLE', 1415953283953, 0, NULL, 0, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787001737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c7708000000100000000174000a706172616d6574657273757200025b42acf317f8060854e002000078700000012baced000573720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00017870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c020000787000000001740033636f6d2e61746c61737369616e2e6a6972612e736572766963652e536572766963654d616e616765723a7365727669636549647571007e0003000000017372000e6a6176612e6c616e672e4c6f6e673b8be490cc8f23df0200014a000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b020000787000000000000027117800');
INSERT INTO jquartz_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.LocalPluginLicenseNotificationJob-job', 'SchedulerServiceTriggers', 'CompatibilityPluginScheduler.JobRunnerKey.LocalPluginLicenseNotificationJob-job-handler', 'SchedulerServiceJobs', false, NULL, 1415996657201, 1415910257201, 5, 'WAITING', 'SIMPLE', 1415910257201, 0, NULL, 0, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787001737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c7708000000100000000174000a706172616d6574657273707800');
INSERT INTO jquartz_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.RemotePluginLicenseNotificationJob-job', 'SchedulerServiceTriggers', 'CompatibilityPluginScheduler.JobRunnerKey.RemotePluginLicenseNotificationJob-job-handler', 'SchedulerServiceJobs', false, NULL, 1415913857213, 1415910257213, 5, 'WAITING', 'SIMPLE', 1415910257213, 0, NULL, 0, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787001737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c7708000000100000000174000a706172616d6574657273707800');
INSERT INTO jquartz_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.PluginUpdateCheckJob-job', 'SchedulerServiceTriggers', 'CompatibilityPluginScheduler.JobRunnerKey.PluginUpdateCheckJob-job-handler', 'SchedulerServiceJobs', false, NULL, 1415974665735, -1, 5, 'WAITING', 'SIMPLE', 1415974665735, 0, NULL, 0, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787001737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c7708000000100000000174000a706172616d6574657273707800');
INSERT INTO jquartz_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.PluginRequestCheckJob-job', 'SchedulerServiceTriggers', 'CompatibilityPluginScheduler.JobRunnerKey.PluginRequestCheckJob-job-handler', 'SchedulerServiceJobs', false, NULL, 1415913857219, 1415910257219, 5, 'WAITING', 'SIMPLE', 1415910257219, 0, NULL, 0, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787001737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c7708000000100000000174000a706172616d6574657273707800');
INSERT INTO jquartz_triggers VALUES (NULL, 'CompatibilityPluginScheduler.JobId.com.atlassian.jira.plugins.dvcs.scheduler.DvcsScheduler:job', 'SchedulerServiceTriggers', 'CompatibilityPluginScheduler.JobRunnerKey.com.atlassian.jira.plugins.dvcs.scheduler.DvcsScheduler', 'SchedulerServiceJobs', false, NULL, 1415913931803, 1415910331803, 5, 'WAITING', 'SIMPLE', 1415910331803, 0, NULL, 0, '\xaced0005737200156f72672e71756172747a2e4a6f62446174614d61709fb083e8bfa9b0cb020000787200266f72672e71756172747a2e7574696c732e537472696e674b65794469727479466c61674d61708208e8c3fbc55d280200015a0013616c6c6f77735472616e7369656e74446174617872001d6f72672e71756172747a2e7574696c732e4469727479466c61674d617013e62ead28760ace0200025a000564697274794c00036d617074000f4c6a6176612f7574696c2f4d61703b787001737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000c7708000000100000000174000a706172616d6574657273707800');


--
-- Data for Name: label; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: licenserolesgroup; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: listenerconfig; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO listenerconfig VALUES (10000, 'com.atlassian.jira.event.listeners.mail.MailListener', 'Mail Listener');
INSERT INTO listenerconfig VALUES (10001, 'com.atlassian.jira.event.listeners.history.IssueAssignHistoryListener', 'Issue Assignment Listener');
INSERT INTO listenerconfig VALUES (10002, 'com.atlassian.jira.event.listeners.search.IssueIndexListener', 'Issue Index Listener');
INSERT INTO listenerconfig VALUES (10200, 'com.atlassian.jira.event.listeners.search.IssueIndexListener', 'Issue Index Listener');


--
-- Data for Name: mailserver; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: managedconfigurationitem; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: membershipbase; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: moved_issue_key; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: nodeassociation; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: nodeindexcounter; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: notification; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO notification VALUES (10028, 10000, NULL, 10, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10029, 10000, NULL, 10, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10030, 10000, NULL, 11, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10031, 10000, NULL, 11, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10032, 10000, NULL, 11, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10033, 10000, NULL, 12, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10034, 10000, NULL, 12, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10035, 10000, NULL, 12, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10036, 10000, NULL, 13, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10037, 10000, NULL, 13, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10038, 10000, NULL, 13, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10100, 10000, NULL, 14, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10101, 10000, NULL, 14, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10102, 10000, NULL, 14, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10103, 10000, NULL, 15, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10104, 10000, NULL, 15, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10105, 10000, NULL, 15, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10106, 10000, NULL, 16, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10107, 10000, NULL, 16, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10108, 10000, NULL, 16, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10000, 10000, NULL, 1, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10001, 10000, NULL, 1, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10002, 10000, NULL, 1, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10003, 10000, NULL, 2, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10004, 10000, NULL, 2, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10005, 10000, NULL, 2, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10006, 10000, NULL, 3, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10007, 10000, NULL, 3, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10008, 10000, NULL, 3, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10009, 10000, NULL, 4, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10010, 10000, NULL, 4, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10011, 10000, NULL, 4, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10012, 10000, NULL, 5, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10013, 10000, NULL, 5, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10014, 10000, NULL, 5, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10015, 10000, NULL, 6, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10016, 10000, NULL, 6, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10017, 10000, NULL, 6, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10018, 10000, NULL, 7, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10019, 10000, NULL, 7, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10020, 10000, NULL, 7, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10021, 10000, NULL, 8, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10022, 10000, NULL, 8, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10023, 10000, NULL, 8, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10024, 10000, NULL, 9, NULL, 'Current_Assignee', NULL);
INSERT INTO notification VALUES (10025, 10000, NULL, 9, NULL, 'Current_Reporter', NULL);
INSERT INTO notification VALUES (10026, 10000, NULL, 9, NULL, 'All_Watchers', NULL);
INSERT INTO notification VALUES (10027, 10000, NULL, 10, NULL, 'Current_Assignee', NULL);


--
-- Data for Name: notificationinstance; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: notificationscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO notificationscheme VALUES (10000, 'Default Notification Scheme', NULL);


--
-- Data for Name: oauthconsumer; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: oauthconsumertoken; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: oauthspconsumer; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: oauthsptoken; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: optionconfiguration; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO optionconfiguration VALUES (10100, 'issuetype', '1', 10000, 0);
INSERT INTO optionconfiguration VALUES (10101, 'issuetype', '2', 10000, 1);
INSERT INTO optionconfiguration VALUES (10102, 'issuetype', '3', 10000, 2);
INSERT INTO optionconfiguration VALUES (10103, 'issuetype', '4', 10000, 3);
INSERT INTO optionconfiguration VALUES (10104, 'issuetype', '5', 10000, 4);


--
-- Data for Name: os_currentstep; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: os_currentstep_prev; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: os_historystep; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: os_historystep_prev; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: os_wfentry; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: permissionscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO permissionscheme VALUES (0, 'Default Permission Scheme', 'This is the default Permission Scheme. Any new projects that are created will be assigned this scheme.');


--
-- Data for Name: pluginstate; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO pluginstate VALUES ('com.atlassian.jira.plugin.system.issueoperations:attach-screenshot', 'false');
INSERT INTO pluginstate VALUES ('com.atlassian.jira.welcome.jira-welcome-plugin:show-whats-new-flag', 'true');


--
-- Data for Name: pluginversion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO pluginversion VALUES (10000, 'ActiveObjects Plugin - OSGi Bundle', 'com.atlassian.activeobjects.activeobjects-plugin', '0.23.7', '2014-11-14 03:23:07.801+07');
INSERT INTO pluginversion VALUES (10001, 'JIRA Active Objects SPI implementation', 'com.atlassian.activeobjects.jira.spi', '0.23.7', '2014-11-14 03:23:07.812+07');
INSERT INTO pluginversion VALUES (10002, 'Atlassian Template Renderer API', 'com.atlassian.templaterenderer.api', '1.5.4', '2014-11-14 03:23:07.817+07');
INSERT INTO pluginversion VALUES (10003, 'Atlassian Template Renderer Velocity 1.6 Plugin', 'com.atlassian.templaterenderer.atlassian-template-renderer-velocity1.6-plugin', '1.5.4', '2014-11-14 03:23:07.821+07');
INSERT INTO pluginversion VALUES (10004, 'Atlassian REST - Module Types', 'com.atlassian.plugins.rest.atlassian-rest-module', '2.9.6', '2014-11-14 03:23:07.827+07');
INSERT INTO pluginversion VALUES (10005, 'Atlassian - Administration - Quick Search - JIRA', 'com.atlassian.administration.atlassian-admin-quicksearch-jira', '1.5', '2014-11-14 03:23:07.861+07');
INSERT INTO pluginversion VALUES (10006, 'Atlassian OAuth Service Provider SPI', 'com.atlassian.oauth.atlassian-oauth-service-provider-spi', '1.9.4', '2014-11-14 03:23:07.869+07');
INSERT INTO pluginversion VALUES (10007, 'Atlassian OAuth Consumer SPI', 'com.atlassian.oauth.atlassian-oauth-consumer-spi', '1.9.4', '2014-11-14 03:23:07.877+07');
INSERT INTO pluginversion VALUES (10008, 'Atlassian JIRA - Plugins - OAuth Consumer SPI', 'com.atlassian.jira.oauth.consumer', '6.3', '2014-11-14 03:23:07.886+07');
INSERT INTO pluginversion VALUES (10009, 'JSON Library', 'com.atlassian.bundles.json-20070829.0.0.1', '20070829.0.0.1', '2014-11-14 03:23:07.889+07');
INSERT INTO pluginversion VALUES (10010, 'Applinks Product Plugin', 'com.atlassian.applinks.applinks-plugin', '4.2.1', '2014-11-14 03:23:08.018+07');
INSERT INTO pluginversion VALUES (10011, 'atlassian-failure-cache-plugin', 'com.atlassian.atlassian-failure-cache-plugin', '0.14', '2014-11-14 03:23:08.026+07');
INSERT INTO pluginversion VALUES (10012, 'Atlassian UI Plugin', 'com.atlassian.auiplugin', '5.6.7-jira-1', '2014-11-14 03:23:08.043+07');
INSERT INTO pluginversion VALUES (10013, 'ICU4J', 'com.atlassian.bundles.icu4j-3.8.0.1', '3.8.0.1', '2014-11-14 03:23:08.051+07');
INSERT INTO pluginversion VALUES (10014, 'Neko HTML', 'com.atlassian.bundles.nekohtml-1.9.12.1', '1.9.12.1', '2014-11-14 03:23:08.059+07');
INSERT INTO pluginversion VALUES (10015, 'Atlassian Embedded Crowd - Administration Plugin', 'com.atlassian.crowd.embedded.admin', '1.7.3', '2014-11-14 03:23:08.087+07');
INSERT INTO pluginversion VALUES (10016, 'Gadget Dashboard Plugin', 'com.atlassian.gadgets.dashboard', '3.5.1', '2014-11-14 03:23:08.123+07');
INSERT INTO pluginversion VALUES (10017, 'Apache HttpCore OSGi bundle', 'org.apache.httpcomponents.httpcore-4.2.4', '4.2.4', '2014-11-14 03:23:08.131+07');
INSERT INTO pluginversion VALUES (10018, 'ROME, RSS and atOM utilitiEs for Java', 'rome.rome-1.0', '1.0', '2014-11-14 03:23:08.135+07');
INSERT INTO pluginversion VALUES (10019, 'Apache HttpClient OSGi bundle', 'org.apache.httpcomponents.httpclient-4.2.5', '4.2.5', '2014-11-14 03:23:08.137+07');
INSERT INTO pluginversion VALUES (10020, 'Gadget Directory Plugin', 'com.atlassian.gadgets.directory', '3.5.1', '2014-11-14 03:23:08.152+07');
INSERT INTO pluginversion VALUES (10021, 'Embedded Gadgets Plugin', 'com.atlassian.gadgets.embedded', '3.5.1', '2014-11-14 03:23:08.161+07');
INSERT INTO pluginversion VALUES (10022, 'Atlassian Gadgets OAuth Service Provider Plugin', 'com.atlassian.gadgets.oauth.serviceprovider', '3.5.1', '2014-11-14 03:23:08.17+07');
INSERT INTO pluginversion VALUES (10023, 'Opensocial Plugin', 'com.atlassian.gadgets.opensocial', '3.5.1', '2014-11-14 03:23:08.188+07');
INSERT INTO pluginversion VALUES (10024, 'Gadget Spec Publisher Plugin', 'com.atlassian.gadgets.publisher', '3.5.1', '2014-11-14 03:23:08.207+07');
INSERT INTO pluginversion VALUES (10025, 'Atlassian HealthCheck Common Module', 'com.atlassian.healthcheck.atlassian-healthcheck', '2.0.7', '2014-11-14 03:23:08.224+07');
INSERT INTO pluginversion VALUES (10026, 'HipChat Core Plugin', 'com.atlassian.hipchat.plugins.core', '0.8.3', '2014-11-14 03:23:08.241+07');
INSERT INTO pluginversion VALUES (10027, 'Atlassian HTTP Client, Apache HTTP components impl', 'com.atlassian.httpclient.atlassian-httpclient-plugin', '0.17.3', '2014-11-14 03:23:08.243+07');
INSERT INTO pluginversion VALUES (10028, 'Apache ServiceMix :: Bundles :: javax.inject', 'org.apache.servicemix.bundles.javax-inject-1.0.0.1', '1.0.0.1', '2014-11-14 03:23:08.245+07');
INSERT INTO pluginversion VALUES (10029, 'Atlassian JIRA - Plugins - REST Plugin', 'com.atlassian.jira.rest', '6.3', '2014-11-14 03:23:08.294+07');
INSERT INTO pluginversion VALUES (10030, 'Universal Plugin Manager - Role-Based Licensing Implementation Plugin', 'com.atlassian.upm.role-based-licensing-plugin', '2.17', '2014-11-14 03:23:08.298+07');
INSERT INTO pluginversion VALUES (10031, 'Atlassian Universal Plugin Manager Plugin', 'com.atlassian.upm.atlassian-universal-plugin-manager-plugin', '2.17', '2014-11-14 03:23:08.334+07');
INSERT INTO pluginversion VALUES (10032, 'JIRA Workflow Designer Plugin', 'com.atlassian.jira.plugins.jira-workflow-designer', '6.3.23', '2014-11-14 03:23:08.346+07');
INSERT INTO pluginversion VALUES (10033, 'JIRA Workflow Sharing Plugin', 'com.atlassian.jira.plugins.workflow.sharing.jira-workflow-sharing-plugin', '1.1.30', '2014-11-14 03:23:08.368+07');
INSERT INTO pluginversion VALUES (10034, 'Project Templates Plugin', 'com.atlassian.jira.project-templates-plugin', '2.41', '2014-11-14 03:23:08.384+07');
INSERT INTO pluginversion VALUES (10035, 'JIRA Core Project Templates Plugin', 'com.atlassian.jira-core-project-templates', '2.41', '2014-11-14 03:23:08.387+07');
INSERT INTO pluginversion VALUES (10036, 'JIRA Issue Collector Plugin', 'com.atlassian.jira.collector.plugin.jira-issue-collector-plugin', '1.4.13', '2014-11-14 03:23:08.406+07');
INSERT INTO pluginversion VALUES (10037, 'RPC JIRA Plugin', 'com.atlassian.jira.ext.rpc', '6.3', '2014-11-14 03:23:08.412+07');
INSERT INTO pluginversion VALUES (10038, 'JIRA iCalendar Plugin', 'com.atlassian.jira.extra.jira-ical-feed', '1.0.16', '2014-11-14 03:23:08.429+07');
INSERT INTO pluginversion VALUES (10039, 'Streams SPI', 'com.atlassian.streams.streams-spi-5.4.1', '5.4.1', '2014-11-14 03:23:08.436+07');
INSERT INTO pluginversion VALUES (10040, 'Atlassian Whitelist API Plugin', 'com.atlassian.plugins.atlassian-whitelist-api-plugin', '1.7', '2014-11-14 03:23:08.442+07');
INSERT INTO pluginversion VALUES (10041, 'Atlassian JIRA - Plugins - Gadgets Plugin', 'com.atlassian.jira.gadgets', '6.3', '2014-11-14 03:23:08.462+07');
INSERT INTO pluginversion VALUES (10042, 'Atlassian JIRA - Plugins - Admin Navigation Component', 'com.atlassian.jira.jira-admin-navigation-plugin', '6.3', '2014-11-14 03:23:08.472+07');
INSERT INTO pluginversion VALUES (10043, 'Atlassian JIRA - Plugins - Application Properties', 'com.atlassian.jira.jira-application-properties-plugin', '6.3', '2014-11-14 03:23:08.48+07');
INSERT INTO pluginversion VALUES (10044, 'JIRA Base URL Plugin', 'com.atlassian.jira.jira-baseurl-plugin', '1.10', '2014-11-14 03:23:08.496+07');
INSERT INTO pluginversion VALUES (10045, 'JIRA Feedback Plugin', 'com.atlassian.jira.jira-feedback-plugin', '1.12', '2014-11-14 03:23:08.504+07');
INSERT INTO pluginversion VALUES (10046, 'Atlassian Navigation Links Plugin', 'com.atlassian.plugins.atlassian-nav-links-plugin', '3.3.4', '2014-11-14 03:23:08.577+07');
INSERT INTO pluginversion VALUES (10047, 'Atlassian JIRA - Plugins - Header Plugin', 'com.atlassian.jira.jira-header-plugin', '6.3', '2014-11-14 03:23:08.616+07');
INSERT INTO pluginversion VALUES (10048, 'Atlassian JIRA - Plugins - Invite User', 'com.atlassian.jira.jira-invite-user-plugin', '1.16', '2014-11-14 03:23:08.635+07');
INSERT INTO pluginversion VALUES (10049, 'Atlassian JIRA - Plugins - Common AppLinks Based Issue Link Plugin', 'com.atlassian.jira.jira-issue-link-applinks-common-plugin', '6.3', '2014-11-14 03:23:08.645+07');
INSERT INTO pluginversion VALUES (10050, 'Atlassian JIRA - Plugins - View Issue Panels', 'com.atlassian.jira.jira-view-issue-plugin', '6.3', '2014-11-14 03:23:08.697+07');
INSERT INTO pluginversion VALUES (10051, 'Atlassian JIRA - Plugins - Confluence Link', 'com.atlassian.jira.jira-issue-link-confluence-plugin', '6.3', '2014-11-14 03:23:08.723+07');
INSERT INTO pluginversion VALUES (10052, 'Atlassian JIRA - Plugins - Remote JIRA Link', 'com.atlassian.jira.jira-issue-link-remote-jira-plugin', '6.3', '2014-11-14 03:23:08.746+07');
INSERT INTO pluginversion VALUES (10053, 'Atlassian JIRA - Plugins - Issue Web Link', 'com.atlassian.jira.jira-issue-link-web-plugin', '6.3', '2014-11-14 03:23:08.749+07');
INSERT INTO pluginversion VALUES (10054, 'jira-issue-nav-components', 'com.atlassian.jira.jira-issue-nav-components', '6.3.20', '2014-11-14 03:23:08.768+07');
INSERT INTO pluginversion VALUES (10055, 'Atlassian JIRA - Plugins - Issue Search', 'com.atlassian.jira.jira-issue-nav-plugin', '6.3.20', '2014-11-14 03:23:08.784+07');
INSERT INTO pluginversion VALUES (10056, 'English (United Kingdom) Language Pack', 'com.atlassian.jira.jira-languages.en_UK', '6.3', '2014-11-14 03:23:08.786+07');
INSERT INTO pluginversion VALUES (10057, 'English (United States) Language Pack', 'com.atlassian.jira.jira-languages.en_US', '6.3', '2014-11-14 03:23:08.788+07');
INSERT INTO pluginversion VALUES (10058, 'Atlassian LESS Transformer Plugin', 'com.atlassian.plugins.less-transformer-plugin', '2.1.1', '2014-11-14 03:23:08.793+07');
INSERT INTO pluginversion VALUES (10059, 'Atlassian JIRA - Plugins - LESS integration', 'com.atlassian.jira.jira-less-integration', '6.3', '2014-11-14 03:23:08.802+07');
INSERT INTO pluginversion VALUES (10060, 'Atlassian JIRA - Plugins - Mail Plugin', 'com.atlassian.jira.jira-mail-plugin', '6.3.10', '2014-11-14 03:23:08.844+07');
INSERT INTO pluginversion VALUES (10061, 'JIRA Monitoring Plugin', 'com.atlassian.jira.jira-monitoring-plugin', '05.6.1', '2014-11-14 03:23:08.878+07');
INSERT INTO pluginversion VALUES (10062, 'Atlassian JIRA - Plugins - My JIRA Home', 'com.atlassian.jira.jira-my-home-plugin', '6.3', '2014-11-14 03:23:08.888+07');
INSERT INTO pluginversion VALUES (10063, 'JIRA Project Config Plugin', 'com.atlassian.jira.jira-project-config-plugin', '6.3.43', '2014-11-14 03:23:09.042+07');
INSERT INTO pluginversion VALUES (10064, 'JIRA Projects Plugin', 'com.atlassian.jira.jira-projects-plugin', '1.1.8', '2014-11-14 03:23:09.067+07');
INSERT INTO pluginversion VALUES (10065, 'Atlassian JIRA - Plugins - Quick Edit Plugin', 'com.atlassian.jira.jira-quick-edit-plugin', '2.0.1', '2014-11-14 03:23:09.078+07');
INSERT INTO pluginversion VALUES (10066, 'Atlassian JIRA - Plugins - Share Content Component', 'com.atlassian.jira.jira-share-plugin', '6.3', '2014-11-14 03:23:09.104+07');
INSERT INTO pluginversion VALUES (10067, 'Atlassian JIRA - Plugins - Closure Template Renderer', 'com.atlassian.jira.jira-soy-plugin', '6.3', '2014-11-14 03:23:09.106+07');
INSERT INTO pluginversion VALUES (10068, 'JIRA Time Zone Detection plugin', 'com.atlassian.jira.jira-tzdetect-plugin', '2.0.2', '2014-11-14 03:23:09.117+07');
INSERT INTO pluginversion VALUES (10069, 'Atlassian JIRA - Plugins - User Profile Plugin', 'com.atlassian.jira.jira-user-profile-plugin', '2.0.1', '2014-11-14 03:23:09.141+07');
INSERT INTO pluginversion VALUES (10070, 'Atlassian JIRA - Plugins - Look And Feel Logo Upload Plugin', 'com.atlassian.jira.lookandfeel', '6.3', '2014-11-14 03:23:09.153+07');
INSERT INTO pluginversion VALUES (10071, 'JIRA Mobile', 'com.atlassian.jira.mobile', '1.6', '2014-11-14 03:23:09.166+07');
INSERT INTO pluginversion VALUES (10072, 'Atlassian JIRA - Plugins - OAuth Service Provider SPI', 'com.atlassian.jira.oauth.serviceprovider', '6.3', '2014-11-14 03:23:09.168+07');
INSERT INTO pluginversion VALUES (10073, 'JIRA Bamboo Plugin', 'com.atlassian.jira.plugin.ext.bamboo', '7.1.11', '2014-11-14 03:23:09.189+07');
INSERT INTO pluginversion VALUES (10074, 'Comment Panel Plugin', 'com.atlassian.jira.plugin.system.comment-panel', '1.0', '2014-11-14 03:23:09.199+07');
INSERT INTO pluginversion VALUES (10075, 'Custom Field Types & Searchers', 'com.atlassian.jira.plugin.system.customfieldtypes', '1.0', '2014-11-14 03:23:09.203+07');
INSERT INTO pluginversion VALUES (10076, 'Issue Operations Plugin', 'com.atlassian.jira.plugin.system.issueoperations', '1.0', '2014-11-14 03:23:09.211+07');
INSERT INTO pluginversion VALUES (10077, 'Issue Tab Panels Plugin', 'com.atlassian.jira.plugin.system.issuetabpanels', '1.0', '2014-11-14 03:23:09.213+07');
INSERT INTO pluginversion VALUES (10078, 'Renderer Plugin', 'com.atlassian.jira.plugin.system.jirarenderers', '1.0', '2014-11-14 03:23:09.215+07');
INSERT INTO pluginversion VALUES (10079, 'System License Roles', 'com.atlassian.jira.plugin.system.license.roles', '1.0', '2014-11-14 03:23:09.216+07');
INSERT INTO pluginversion VALUES (10080, 'Project Role Actors Plugin', 'com.atlassian.jira.plugin.system.projectroleactors', '1.0', '2014-11-14 03:23:09.218+07');
INSERT INTO pluginversion VALUES (10081, 'Wiki Renderer Macros Plugin', 'com.atlassian.jira.plugin.system.renderers.wiki.macros', '1.0', '2014-11-14 03:23:09.219+07');
INSERT INTO pluginversion VALUES (10082, 'Reports Plugin', 'com.atlassian.jira.plugin.system.reports', '1.0', '2014-11-14 03:23:09.221+07');
INSERT INTO pluginversion VALUES (10083, 'Workflow Plugin', 'com.atlassian.jira.plugin.system.workflow', '1.0', '2014-11-14 03:23:09.224+07');
INSERT INTO pluginversion VALUES (10084, 'JIRA Workflow Transition Tabs', 'com.atlassian.jira.plugin.system.workfloweditor.transition.tabs', '1.0', '2014-11-14 03:23:09.226+07');
INSERT INTO pluginversion VALUES (10085, 'Content Link Resolvers Plugin', 'com.atlassian.jira.plugin.wiki.contentlinkresolvers', '1.0', '2014-11-14 03:23:09.228+07');
INSERT INTO pluginversion VALUES (10086, 'Renderer Component Factories Plugin', 'com.atlassian.jira.plugin.wiki.renderercomponentfactories', '1.0', '2014-11-14 03:23:09.231+07');
INSERT INTO pluginversion VALUES (10087, 'JIRA Agile Marketing Plugin', 'com.atlassian.jira.plugins.greenhopper-marketing-plugin', '1.0.14', '2014-11-14 03:23:09.276+07');
INSERT INTO pluginversion VALUES (10088, 'scala-provider-plugin', 'com.atlassian.scala.plugins.scala-2.10-provider-plugin', '0.5', '2014-11-14 03:23:09.278+07');
INSERT INTO pluginversion VALUES (10089, 'jackson-module-scala-2.10-provider', 'com.atlassian.scala.plugins.jackson-module-scala-2.10-provider-plugin', '0.5', '2014-11-14 03:23:09.279+07');
INSERT INTO pluginversion VALUES (10090, 'jira-inline-issue-create-plugin', 'com.atlassian.jira.plugins.inline-create.jira-inline-issue-create-plugin', '0.2.2', '2014-11-14 03:23:09.29+07');
INSERT INTO pluginversion VALUES (10091, 'Atlassian JIRA - Admin Helper Plugin', 'com.atlassian.jira.plugins.jira-admin-helper-plugin', '1.18.5', '2014-11-14 03:23:09.313+07');
INSERT INTO pluginversion VALUES (10092, 'JIRA Auditing Plugin', 'com.atlassian.jira.plugins.jira-auditing-plugin', '1.4.3', '2014-11-14 03:23:09.324+07');
INSERT INTO pluginversion VALUES (10093, 'Streams API', 'com.atlassian.streams.streams-api-5.4.1', '5.4.1', '2014-11-14 03:23:09.326+07');
INSERT INTO pluginversion VALUES (10094, 'JIRA DVCS Connector Plugin', 'com.atlassian.jira.plugins.jira-bitbucket-connector-plugin', '2.1.15', '2014-11-14 03:23:09.366+07');
INSERT INTO pluginversion VALUES (10095, 'Atlassian Remote Event Common Plugin', 'com.atlassian.plugins.atlassian-remote-event-common-plugin', '0.6.1', '2014-11-14 03:23:09.381+07');
INSERT INTO pluginversion VALUES (10096, 'Atlassian JIRA - Plugins - Development Integration Plugin', 'com.atlassian.jira.plugins.jira-development-integration-plugin', '2.2.6', '2014-11-14 03:23:09.448+07');
INSERT INTO pluginversion VALUES (10097, 'Atlassian JIRA - Plugins - Healthcheck Plugin', 'com.atlassian.jira.plugins.jira-healthcheck-plugin', '1.0.10', '2014-11-14 03:23:09.453+07');
INSERT INTO pluginversion VALUES (10098, 'JIRA Importers Plugin (JIM)', 'com.atlassian.jira.plugins.jira-importers-plugin', '6.1.12', '2014-11-14 03:23:09.481+07');
INSERT INTO pluginversion VALUES (10099, 'Bitbucket Importer Plugin for JIM', 'com.atlassian.jira.plugins.jira-importers-bitbucket-plugin', '1.0.8', '2014-11-14 03:23:09.49+07');
INSERT INTO pluginversion VALUES (10100, 'JIRA GitHub Issue Importer', 'com.atlassian.jira.plugins.jira-importers-github-plugin', '2.0.2', '2014-11-14 03:23:09.5+07');
INSERT INTO pluginversion VALUES (10101, 'Redmine Importers Plugin for JIM', 'com.atlassian.jira.plugins.jira-importers-redmine-plugin', '2.0.7', '2014-11-14 03:23:09.503+07');
INSERT INTO pluginversion VALUES (10102, 'JIRA Password Policy Plugin', 'com.atlassian.jira.plugins.jira-password-policy-plugin', '1.1.2', '2014-11-14 03:23:09.509+07');
INSERT INTO pluginversion VALUES (10103, 'JIRA for Software Plugin', 'com.atlassian.jira.plugins.jira-software-plugin', '0.9', '2014-11-14 03:23:09.523+07');
INSERT INTO pluginversion VALUES (10104, 'Atlassian JIRA - Plugins - Transition Trigger Plugin', 'com.atlassian.jira.plugins.jira-transition-triggers-plugin', '2.2.5', '2014-11-14 03:23:09.546+07');
INSERT INTO pluginversion VALUES (10105, 'Atlassian WebHooks Plugin', 'com.atlassian.webhooks.atlassian-webhooks-plugin', '0.17.6', '2014-11-14 03:23:09.573+07');
INSERT INTO pluginversion VALUES (10106, 'JIRA WebHooks Plugin', 'com.atlassian.jira.plugins.webhooks.jira-webhooks-plugin', '1.2.6', '2014-11-14 03:23:09.591+07');
INSERT INTO pluginversion VALUES (10107, 'JIRA JSON-RPC Plugin', 'com.atlassian.jira.rpc.jira-json-rpc-plugin', '1.0.4', '2014-11-14 03:23:09.812+07');
INSERT INTO pluginversion VALUES (10108, 'JIRA Welcome Plugin', 'com.atlassian.jira.welcome.jira-welcome-plugin', '1.1.53', '2014-11-14 03:23:09.84+07');
INSERT INTO pluginversion VALUES (10109, 'FishEye Plugin', 'com.atlassian.jirafisheyeplugin', '6.3.4', '2014-11-14 03:23:09.886+07');
INSERT INTO pluginversion VALUES (10110, 'Wallboard Plugin', 'com.atlassian.jirawallboard.atlassian-wallboard-plugin', '1.8.9', '2014-11-14 03:23:09.902+07');
INSERT INTO pluginversion VALUES (10111, 'Atlassian Bot Session Killer', 'com.atlassian.labs.atlassian-bot-killer', '1.7.5', '2014-11-14 03:23:09.913+07');
INSERT INTO pluginversion VALUES (10112, 'HipChat for JIRA', 'com.atlassian.labs.hipchat.hipchat-for-jira-plugin', '1.2.11', '2014-11-14 03:23:09.923+07');
INSERT INTO pluginversion VALUES (10113, 'Workbox - Common Plugin', 'com.atlassian.mywork.mywork-common-plugin', '1.8.1', '2014-11-14 03:23:09.942+07');
INSERT INTO pluginversion VALUES (10114, 'Workbox - JIRA Provider Plugin', 'com.atlassian.mywork.mywork-jira-provider-plugin', '1.8.1', '2014-11-14 03:23:09.95+07');
INSERT INTO pluginversion VALUES (10115, 'Atlassian OAuth Admin Plugin', 'com.atlassian.oauth.admin', '1.9.4', '2014-11-14 03:23:09.951+07');
INSERT INTO pluginversion VALUES (10116, 'Atlassian OAuth Consumer Plugin', 'com.atlassian.oauth.consumer', '1.9.4', '2014-11-14 03:23:09.954+07');
INSERT INTO pluginversion VALUES (10117, 'Atlassian OAuth Service Provider Plugin', 'com.atlassian.oauth.serviceprovider', '1.9.4', '2014-11-14 03:23:09.968+07');
INSERT INTO pluginversion VALUES (10118, 'Atlassian Awareness Capability', 'com.atlassian.plugins.atlassian-awareness-capability', '0.0.5', '2014-11-14 03:23:09.985+07');
INSERT INTO pluginversion VALUES (10119, 'Atlassian Plugins - Web Resources - Implementation Plugin', 'com.atlassian.plugins.atlassian-plugins-webresource-plugin', '3.1.0', '2014-11-14 03:23:10.001+07');
INSERT INTO pluginversion VALUES (10120, 'Project Creation Capability Product REST Plugin', 'com.atlassian.plugins.atlassian-project-creation-plugin', '1.2.11', '2014-11-14 03:23:10.02+07');
INSERT INTO pluginversion VALUES (10121, 'Atlassian Remote Event Consumer Plugin', 'com.atlassian.plugins.atlassian-remote-event-consumer-plugin', '0.6.1', '2014-11-14 03:23:10.033+07');
INSERT INTO pluginversion VALUES (10122, 'Atlassian Whitelist UI Plugin', 'com.atlassian.plugins.atlassian-whitelist-ui-plugin', '1.7', '2014-11-14 03:23:10.045+07');
INSERT INTO pluginversion VALUES (10123, 'jira-help-tips', 'com.atlassian.plugins.helptips.jira-help-tips', '0.45', '2014-11-14 03:23:10.062+07');
INSERT INTO pluginversion VALUES (10124, 'Issue Status Plugin', 'com.atlassian.plugins.issue-status-plugin', '1.1.6', '2014-11-14 03:23:10.07+07');
INSERT INTO pluginversion VALUES (10125, 'Attach Image for JIRA', 'com.atlassian.plugins.jira-html5-attach-images', '1.5.4', '2014-11-14 03:23:10.093+07');
INSERT INTO pluginversion VALUES (10126, 'Project Creation Plugin SPI for JIRA', 'com.atlassian.plugins.jira-project-creation', '1.2.11', '2014-11-14 03:23:10.101+07');
INSERT INTO pluginversion VALUES (10127, 'Remote Link Aggregator Plugin', 'com.atlassian.plugins.remote-link-aggregator-plugin', '2.0.9', '2014-11-14 03:23:10.118+07');
INSERT INTO pluginversion VALUES (10128, 'JIRA Remote Link Aggregator Plugin', 'com.atlassian.plugins.jira-remote-link-aggregator-plugin', '2.0.9', '2014-11-14 03:23:10.127+07');
INSERT INTO pluginversion VALUES (10129, 'jquery', 'com.atlassian.plugins.jquery', '1.7.2', '2014-11-14 03:23:10.135+07');
INSERT INTO pluginversion VALUES (10130, 'Atlassian Pretty URLs Plugin', 'com.atlassian.prettyurls.atlassian-pretty-urls-plugin', '1.11.0', '2014-11-14 03:23:10.146+07');
INSERT INTO pluginversion VALUES (10131, 'Atlassian JIRA - Plugins - SAL Plugin', 'com.atlassian.sal.jira', '6.3', '2014-11-14 03:23:10.155+07');
INSERT INTO pluginversion VALUES (10132, 'Atlassian Soy - Plugin', 'com.atlassian.soy.soy-template-plugin', '2.7.0', '2014-11-14 03:23:10.163+07');
INSERT INTO pluginversion VALUES (10133, 'Streams Plugin', 'com.atlassian.streams', '5.4.1', '2014-11-14 03:23:10.19+07');
INSERT INTO pluginversion VALUES (10134, 'Streams Inline Actions Plugin', 'com.atlassian.streams.actions', '5.4.1', '2014-11-14 03:23:10.193+07');
INSERT INTO pluginversion VALUES (10135, 'Streams Core Plugin', 'com.atlassian.streams.core', '5.4.1', '2014-11-14 03:23:10.195+07');
INSERT INTO pluginversion VALUES (10136, 'JIRA Streams Inline Actions Plugin', 'com.atlassian.streams.jira.inlineactions', '5.4.1', '2014-11-14 03:23:10.196+07');
INSERT INTO pluginversion VALUES (10137, 'JIRA Activity Stream Plugin', 'com.atlassian.streams.streams-jira-plugin', '5.4.1', '2014-11-14 03:23:10.231+07');
INSERT INTO pluginversion VALUES (10138, 'Streams Third Party Provider Plugin', 'com.atlassian.streams.streams-thirdparty-plugin', '5.4.1', '2014-11-14 03:23:10.245+07');
INSERT INTO pluginversion VALUES (10139, 'Support Tools Plugin', 'com.atlassian.support.stp', '3.5.14', '2014-11-14 03:23:10.268+07');
INSERT INTO pluginversion VALUES (10140, 'ROME: RSS/Atom syndication and publishing tools', 'com.springsource.com.sun.syndication-0.9.0', '0.9.0', '2014-11-14 03:23:10.27+07');
INSERT INTO pluginversion VALUES (10141, 'JDOM DOM Processor', 'com.springsource.org.jdom-1.0.0', '1.0.0', '2014-11-14 03:23:10.271+07');
INSERT INTO pluginversion VALUES (10142, 'Crowd REST API', 'crowd-rest-application-management', '1.0', '2014-11-14 03:23:10.293+07');
INSERT INTO pluginversion VALUES (10143, 'Crowd REST API', 'crowd-rest-plugin', '1.0', '2014-11-14 03:23:10.313+07');
INSERT INTO pluginversion VALUES (10144, 'Crowd System Password Encoders', 'crowd.system.passwordencoders', '1.0', '2014-11-14 03:23:10.322+07');
INSERT INTO pluginversion VALUES (10145, 'JIRA Footer', 'jira.footer', '1.0', '2014-11-14 03:23:10.33+07');
INSERT INTO pluginversion VALUES (10146, 'Help Paths Plugin', 'jira.help.paths', '1.0', '2014-11-14 03:23:10.338+07');
INSERT INTO pluginversion VALUES (10147, 'Issue Views Plugin', 'jira.issueviews', '1.0', '2014-11-14 03:23:10.341+07');
INSERT INTO pluginversion VALUES (10148, 'JQL Functions', 'jira.jql.function', '1.0', '2014-11-14 03:23:10.344+07');
INSERT INTO pluginversion VALUES (10149, 'Keyboard Shortcuts Plugin', 'jira.keyboard.shortcuts', '1.0', '2014-11-14 03:23:10.348+07');
INSERT INTO pluginversion VALUES (10150, 'JIRA Global Permissions', 'jira.system.global.permissions', '1.0', '2014-11-14 03:23:10.35+07');
INSERT INTO pluginversion VALUES (10151, 'JIRA Project Permissions', 'jira.system.project.permissions', '1.0', '2014-11-14 03:23:10.354+07');
INSERT INTO pluginversion VALUES (10152, 'Top Navigation Bar', 'jira.top.navigation.bar', '1.0', '2014-11-14 03:23:10.358+07');
INSERT INTO pluginversion VALUES (10153, 'JIRA Usage Hints', 'jira.usage.hints', '1.0', '2014-11-14 03:23:10.36+07');
INSERT INTO pluginversion VALUES (10154, 'User Format', 'jira.user.format', '1.0', '2014-11-14 03:23:10.363+07');
INSERT INTO pluginversion VALUES (10155, 'User Profile Panels', 'jira.user.profile.panels', '1.0', '2014-11-14 03:23:10.364+07');
INSERT INTO pluginversion VALUES (10156, 'Admin Menu Sections', 'jira.webfragments.admin', '1.0', '2014-11-14 03:23:10.378+07');
INSERT INTO pluginversion VALUES (10157, 'Browse Project Operations Sections', 'jira.webfragments.browse.project.links', '1.0', '2014-11-14 03:23:10.381+07');
INSERT INTO pluginversion VALUES (10158, 'Preset Filters Sections', 'jira.webfragments.preset.filters', '1.0', '2014-11-14 03:23:10.383+07');
INSERT INTO pluginversion VALUES (10159, 'User Navigation Bar Sections', 'jira.webfragments.user.navigation.bar', '1.0', '2014-11-14 03:23:10.386+07');
INSERT INTO pluginversion VALUES (10160, 'User Profile Links', 'jira.webfragments.user.profile.links', '1.0', '2014-11-14 03:23:10.387+07');
INSERT INTO pluginversion VALUES (10161, 'View Project Operations Sections', 'jira.webfragments.view.project.operations', '1.0', '2014-11-14 03:23:10.389+07');
INSERT INTO pluginversion VALUES (10162, 'Web Resources Plugin', 'jira.webresources', '1.0', '2014-11-14 03:23:10.4+07');
INSERT INTO pluginversion VALUES (10163, 'Sisu-Inject', 'org.eclipse.sisu.inject-0.0.0.atlassian6', '0.0.0.atlassian6', '2014-11-14 03:23:10.401+07');
INSERT INTO pluginversion VALUES (10164, 'ASM', 'org.objectweb.asm-3.3.1.v201105211655', '3.3.1.v201105211655', '2014-11-14 03:23:10.403+07');
INSERT INTO pluginversion VALUES (10165, 'sisu-guice', 'org.sonatype.sisu.guice-3.1.3', '3.1.3', '2014-11-14 03:23:10.404+07');
INSERT INTO pluginversion VALUES (10166, 'JIRA German (Germany) Language Pack', 'tac.jira.languages.de_DE', '6.3.0-m06-v2r8533-2014-06-13', '2014-11-14 03:23:10.406+07');
INSERT INTO pluginversion VALUES (10167, 'JIRA Spanish (Spain) Language Pack', 'tac.jira.languages.es_ES', '6.3.0-m06-v2r17546-2014-06-16', '2014-11-14 03:23:10.407+07');
INSERT INTO pluginversion VALUES (10168, 'JIRA French (France) Language Pack', 'tac.jira.languages.fr_FR', '6.3.0-m06-v2r5012-2014-06-13', '2014-11-14 03:23:10.408+07');
INSERT INTO pluginversion VALUES (10169, 'JIRA Japanese (Japan) Language Pack', 'tac.jira.languages.ja_JP', '6.3.0-m06-v2r19541-2014-06-18', '2014-11-14 03:23:10.41+07');


--
-- Data for Name: portalpage; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO portalpage VALUES (10000, NULL, 'System Dashboard', NULL, 0, 0, 'AA', 0);


--
-- Data for Name: portletconfiguration; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO portletconfiguration VALUES (10000, 10000, NULL, 0, 0, 'rest/gadgets/1.0/g/com.atlassian.jira.gadgets:introduction-gadget/gadgets/introduction-gadget.xml', NULL);
INSERT INTO portletconfiguration VALUES (10001, 10000, NULL, 0, 1, 'rest/gadgets/1.0/g/com.atlassian.jira.gadgets:admin-gadget/gadgets/admin-gadget.xml', NULL);
INSERT INTO portletconfiguration VALUES (10002, 10000, NULL, 1, 0, 'rest/gadgets/1.0/g/com.atlassian.jira.gadgets:assigned-to-me-gadget/gadgets/assigned-to-me-gadget.xml', NULL);
INSERT INTO portletconfiguration VALUES (10003, 10000, NULL, 1, 1, 'rest/gadgets/1.0/g/com.atlassian.streams.streams-jira-plugin:activitystream-gadget/gadgets/activitystream-gadget.xml', NULL);


--
-- Data for Name: priority; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO priority VALUES ('1', 1, 'Blocker', 'Blocks development and/or testing work, production could not run.', '/images/icons/priorities/blocker.png', '#cc0000');
INSERT INTO priority VALUES ('2', 2, 'Critical', 'Crashes, loss of data, severe memory leak.', '/images/icons/priorities/critical.png', '#ff0000');
INSERT INTO priority VALUES ('3', 3, 'Major', 'Major loss of function.', '/images/icons/priorities/major.png', '#009900');
INSERT INTO priority VALUES ('4', 4, 'Minor', 'Minor loss of function, or other problem where easy workaround is present.', '/images/icons/priorities/minor.png', '#006600');
INSERT INTO priority VALUES ('5', 5, 'Trivial', 'Cosmetic problem like misspelt words or misaligned text.', '/images/icons/priorities/trivial.png', '#003300');


--
-- Data for Name: productlicense; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: project; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: project_key; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: projectcategory; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: projectrole; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO projectrole VALUES (10000, 'Users', 'A project role that represents users in a project');
INSERT INTO projectrole VALUES (10001, 'Developers', 'A project role that represents developers in a project');
INSERT INTO projectrole VALUES (10002, 'Administrators', 'A project role that represents administrators in a project');


--
-- Data for Name: projectroleactor; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO projectroleactor VALUES (10000, NULL, 10000, 'atlassian-group-role-actor', 'jira-users');
INSERT INTO projectroleactor VALUES (10001, NULL, 10001, 'atlassian-group-role-actor', 'jira-developers');
INSERT INTO projectroleactor VALUES (10002, NULL, 10002, 'atlassian-group-role-actor', 'jira-administrators');


--
-- Data for Name: projectversion; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: propertydata; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: propertydate; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: propertydecimal; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: propertyentry; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO propertyentry VALUES (1, 'jira.properties', 1, 'jira.version.patched', 5);
INSERT INTO propertyentry VALUES (3, 'jira.properties', 1, 'jira.avatar.default.id', 5);
INSERT INTO propertyentry VALUES (5, 'jira.properties', 1, 'jira.avatar.user.default.id', 5);
INSERT INTO propertyentry VALUES (6, 'jira.properties', 1, 'jira.avatar.user.anonymous.id', 5);
INSERT INTO propertyentry VALUES (7, 'jira.properties', 1, 'jira.scheme.default.issue.type', 5);
INSERT INTO propertyentry VALUES (8, 'jira.properties', 1, 'jira.constant.default.resolution', 5);
INSERT INTO propertyentry VALUES (9, 'jira.properties', 1, 'jira.whitelist.disabled', 1);
INSERT INTO propertyentry VALUES (10, 'jira.properties', 1, 'jira.whitelist.rules', 6);
INSERT INTO propertyentry VALUES (11, 'jira.properties', 1, 'jira.option.timetracking', 1);
INSERT INTO propertyentry VALUES (12, 'jira.properties', 1, 'jira.timetracking.estimates.legacy.behaviour', 1);
INSERT INTO propertyentry VALUES (13, 'jira.properties', 1, 'jira.version', 5);
INSERT INTO propertyentry VALUES (14, 'jira.properties', 1, 'jira.downgrade.minimum.version', 5);
INSERT INTO propertyentry VALUES (15, 'jira.properties', 1, 'jira.option.allowunassigned', 1);
INSERT INTO propertyentry VALUES (21, 'com.atlassian.jira.plugins.jira-workflow-designer', 1, 'jira.workflow.layout:8a6044147cf2c19c02d099279cfbfd47', 6);
INSERT INTO propertyentry VALUES (10100, 'jira.properties', 1, 'jira.i18n.language.index', 5);
INSERT INTO propertyentry VALUES (10101, 'jira.properties', 1, 'jira.sid.key', 5);
INSERT INTO propertyentry VALUES (10102, 'jira.properties', 1, 'jira.avatar.issuetype.default.id', 5);
INSERT INTO propertyentry VALUES (10103, 'jira.properties', 1, 'jira.avatar.issuetype.subtask.default.id', 5);
INSERT INTO propertyentry VALUES (10200, 'jira.properties', 1, 'jira.webresource.flushcounter', 5);
INSERT INTO propertyentry VALUES (10201, 'jira.properties', 1, 'jira.mail.send.disabled', 1);
INSERT INTO propertyentry VALUES (10202, 'jira.properties', 1, 'mailsetting.jira.mail.send.disabled.modifiedBy', 5);
INSERT INTO propertyentry VALUES (10203, 'jira.properties', 1, 'mailsetting.jira.mail.send.disabled.modifiedDate', 5);
INSERT INTO propertyentry VALUES (10204, 'jira.properties', 1, 'webwork.i18n.encoding', 5);
INSERT INTO propertyentry VALUES (10205, 'jira.properties', 1, 'jira.title', 5);
INSERT INTO propertyentry VALUES (10206, 'jira.properties', 1, 'jira.baseurl', 5);
INSERT INTO propertyentry VALUES (10207, 'jira.properties', 1, 'jira.mode', 5);
INSERT INTO propertyentry VALUES (10208, 'jira.properties', 1, 'jira.path.index.use.default.directory', 1);
INSERT INTO propertyentry VALUES (10209, 'jira.properties', 1, 'jira.option.indexing', 1);
INSERT INTO propertyentry VALUES (10210, 'jira.properties', 1, 'jira.path.attachments', 5);
INSERT INTO propertyentry VALUES (10211, 'jira.properties', 1, 'jira.path.attachments.use.default.directory', 1);
INSERT INTO propertyentry VALUES (10212, 'jira.properties', 1, 'jira.option.allowattachments', 1);
INSERT INTO propertyentry VALUES (10213, 'ServiceConfig', 10001, 'USE_DEFAULT_DIRECTORY', 5);
INSERT INTO propertyentry VALUES (10214, 'jira.properties', 1, 'jira.path.backup', 5);
INSERT INTO propertyentry VALUES (10215, 'jira.properties', 1, 'License20', 6);
INSERT INTO propertyentry VALUES (10216, 'jira.properties', 1, 'jira.edition', 5);
INSERT INTO propertyentry VALUES (10217, 'jira.properties', 1, 'org.apache.shindig.common.crypto.BlobCrypter:key', 5);
INSERT INTO propertyentry VALUES (10218, 'BambooServerProperties', 1, 'bamboo.config.version', 2);
INSERT INTO propertyentry VALUES (10220, 'jira.properties', 1, 'AO_4AEACD_#', 5);
INSERT INTO propertyentry VALUES (10222, 'jira.properties', 1, 'jira.webresource.superbatch.flushcounter', 5);
INSERT INTO propertyentry VALUES (10223, 'jira.properties', 1, 'com.atlassian.jira.util.index.IndexingCounterManagerImpl.counterValue', 3);
INSERT INTO propertyentry VALUES (10224, 'jira.properties', 1, 'jira.setup', 5);
INSERT INTO propertyentry VALUES (10225, 'jira.properties', 1, 'jira.option.user.externalmanagement', 1);
INSERT INTO propertyentry VALUES (10226, 'jira.properties', 1, 'jira.option.voting', 1);
INSERT INTO propertyentry VALUES (10227, 'jira.properties', 1, 'jira.option.watching', 1);
INSERT INTO propertyentry VALUES (10228, 'jira.properties', 1, 'jira.option.issuelinking', 1);
INSERT INTO propertyentry VALUES (10229, 'jira.properties', 1, 'jira.option.emailvisible', 5);
INSERT INTO propertyentry VALUES (10230, 'jira.properties', 1, 'jira.option.allowsubtasks', 1);
INSERT INTO propertyentry VALUES (10231, 'fisheye-jira-plugin.properties', 1, 'FISH-375-fixed', 5);
INSERT INTO propertyentry VALUES (10232, 'fisheye-jira-plugin.properties', 1, 'fisheye.ual.migration.complete', 5);
INSERT INTO propertyentry VALUES (10233, 'fisheye-jira-plugin.properties', 1, 'fisheye.ual.crucible.enabled.property.fix.complete', 5);
INSERT INTO propertyentry VALUES (10235, 'jira.properties', 1, 'com.atlassian.sal.jira:build', 5);
INSERT INTO propertyentry VALUES (10237, 'jira.properties', 1, 'com.atlassian.jira.jira-mail-plugin:build', 5);
INSERT INTO propertyentry VALUES (10238, 'jira.properties', 1, 'com.atlassian.plugins.custom_apps.hasCustomOrder', 5);
INSERT INTO propertyentry VALUES (10239, 'jira.properties', 1, 'com.atlassian.plugins.atlassian-nav-links-plugin:build', 5);
INSERT INTO propertyentry VALUES (10241, 'jira.properties', 1, 'com.atlassian.jira.project-templates-plugin:build', 5);
INSERT INTO propertyentry VALUES (10242, 'jira.properties', 1, 'com.atlassian.jira.plugins.jira-importers-plugin:build', 5);
INSERT INTO propertyentry VALUES (10243, 'jira.properties', 1, 'com.atlassian.jira.plugin.ext.bamboo:build', 5);
INSERT INTO propertyentry VALUES (10247, 'jira.properties', 1, 'com.atlassian.upm:notifications:dismissal-plugin.request', 5);
INSERT INTO propertyentry VALUES (10249, 'jira.properties', 1, 'com.atlassian.upm:notifications:dismissal-evaluation.expired', 5);
INSERT INTO propertyentry VALUES (10251, 'jira.properties', 1, 'com.atlassian.upm:notifications:dismissal-edition.mismatch', 5);
INSERT INTO propertyentry VALUES (10300, 'jira.properties', 1, 'com.atlassian.upm:notifications:notification-edition.mismatch', 5);
INSERT INTO propertyentry VALUES (10301, 'jira.properties', 1, 'com.atlassian.upm:notifications:notification-evaluation.expired', 5);
INSERT INTO propertyentry VALUES (10302, 'jira.properties', 1, 'com.atlassian.upm:notifications:notification-evaluation.nearlyexpired', 5);
INSERT INTO propertyentry VALUES (10303, 'jira.properties', 1, 'com.atlassian.upm:notifications:notification-plugin.request', 5);
INSERT INTO propertyentry VALUES (10306, 'jira.properties', 1, 'com.atlassian.upm.log.PluginSettingsAuditLogService:log:upm_audit_log_v3', 5);
INSERT INTO propertyentry VALUES (10252, 'jira.properties', 1, 'com.atlassian.upm:notifications:dismissal-maintenance.expired', 5);
INSERT INTO propertyentry VALUES (10254, 'jira.properties', 1, 'com.atlassian.upm:notifications:dismissal-new.licenses', 5);
INSERT INTO propertyentry VALUES (10255, 'jira.properties', 1, 'com.atlassian.upm:notifications:dismissal-updated.licenses', 5);
INSERT INTO propertyentry VALUES (10256, 'jira.properties', 1, 'com.atlassian.upm:notifications:dismissal-auto.updated.plugin', 5);
INSERT INTO propertyentry VALUES (10257, 'jira.properties', 1, 'com.atlassian.upm:notifications:dismissal-auto.updated.upm', 5);
INSERT INTO propertyentry VALUES (10258, 'jira.properties', 1, 'com.atlassian.upm.request.PluginSettingsPluginRequestStore:requests:requests_v2', 5);
INSERT INTO propertyentry VALUES (10261, 'jira.properties', 1, 'com.atlassian.upm.atlassian-universal-plugin-manager-plugin:build', 5);
INSERT INTO propertyentry VALUES (10262, 'jira.properties', 1, 'com.atlassian.jira.plugins.jira-workflow-designer:build', 5);
INSERT INTO propertyentry VALUES (10264, 'jira.properties', 1, 'com.atlassian.plugins.atlassian-whitelist-api-plugin:whitelist.enabled', 5);
INSERT INTO propertyentry VALUES (10266, 'jira.properties', 1, 'com.atlassian.plugins.atlassian-whitelist-api-plugin:build', 5);
INSERT INTO propertyentry VALUES (10269, 'jira.properties', 1, 'com.atlassian.jira.plugins.webhooks.jira-webhooks-plugin:build', 5);
INSERT INTO propertyentry VALUES (10280, 'jira.properties', 1, 'dvcs.connector.bitbucket.url', 5);
INSERT INTO propertyentry VALUES (10281, 'jira.properties', 1, 'dvcs.connector.github.url', 5);
INSERT INTO propertyentry VALUES (10284, 'jira.properties', 1, 'AO_E8B6CC_#', 5);
INSERT INTO propertyentry VALUES (10285, 'jira.properties', 1, 'com.atlassian.jira.plugins.jira-bitbucket-connector-plugin:build', 5);
INSERT INTO propertyentry VALUES (10286, 'jira.properties', 1, 'com.atlassian.jira.lookandfeel:isDefaultFavicon', 5);
INSERT INTO propertyentry VALUES (10287, 'jira.properties', 1, 'com.atlassian.jira.lookandfeel:usingCustomFavicon', 5);
INSERT INTO propertyentry VALUES (10288, 'jira.properties', 1, 'com.atlassian.jira.lookandfeel:customDefaultFaviconURL', 5);
INSERT INTO propertyentry VALUES (10289, 'jira.properties', 1, 'com.atlassian.jira.lookandfeel:customDefaultFaviconHiresURL', 5);
INSERT INTO propertyentry VALUES (10290, 'jira.properties', 1, 'com.atlassian.jira.lookandfeel:faviconWidth', 5);
INSERT INTO propertyentry VALUES (10291, 'jira.properties', 1, 'com.atlassian.jira.lookandfeel:faviconHeight', 5);
INSERT INTO propertyentry VALUES (10292, 'jira.properties', 1, 'jira.lf.favicon.url', 5);
INSERT INTO propertyentry VALUES (10293, 'jira.properties', 1, 'jira.lf.favicon.hires.url', 5);
INSERT INTO propertyentry VALUES (10294, 'jira.properties', 1, 'com.atlassian.jira.lookandfeel:build', 5);
INSERT INTO propertyentry VALUES (10296, 'jira.properties', 1, 'com.atlassian.crowd.embedded.admin:build', 5);
INSERT INTO propertyentry VALUES (10297, 'jira.properties', 1, 'com.atlassian.jira.gadgets:build', 5);
INSERT INTO propertyentry VALUES (10298, 'jira.properties', 1, 'com.atlassian.jirawallboard.atlassian-wallboard-plugin:build', 5);
INSERT INTO propertyentry VALUES (10299, 'jira.properties', 1, 'jira-header-plugin.studio-tab-migration-complete', 5);
INSERT INTO propertyentry VALUES (10304, 'jira.properties', 1, 'com.atlassian.upm:notifications:notification-maintenance.expired', 5);
INSERT INTO propertyentry VALUES (10305, 'jira.properties', 1, 'com.atlassian.upm:notifications:notification-maintenance.nearlyexpired', 5);
INSERT INTO propertyentry VALUES (10307, 'jira.properties', 1, 'com.atlassian.activeobjects.admin.ActiveObjectsPluginToTablesMapping', 6);
INSERT INTO propertyentry VALUES (10308, 'jira.properties', 1, 'com.atlassian.upm:notifications:notification-update', 6);


--
-- Data for Name: propertynumber; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO propertynumber VALUES (9, 0);
INSERT INTO propertynumber VALUES (11, 1);
INSERT INTO propertynumber VALUES (12, 0);
INSERT INTO propertynumber VALUES (15, 1);
INSERT INTO propertynumber VALUES (10201, 0);
INSERT INTO propertynumber VALUES (10208, 1);
INSERT INTO propertynumber VALUES (10209, 1);
INSERT INTO propertynumber VALUES (10211, 1);
INSERT INTO propertynumber VALUES (10212, 1);
INSERT INTO propertynumber VALUES (10218, 22);
INSERT INTO propertynumber VALUES (10223, 0);
INSERT INTO propertynumber VALUES (10225, 0);
INSERT INTO propertynumber VALUES (10226, 1);
INSERT INTO propertynumber VALUES (10227, 1);
INSERT INTO propertynumber VALUES (10228, 1);
INSERT INTO propertynumber VALUES (10230, 1);


--
-- Data for Name: propertystring; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO propertystring VALUES (3, '10011');
INSERT INTO propertystring VALUES (5, '10122');
INSERT INTO propertystring VALUES (6, '10123');
INSERT INTO propertystring VALUES (7, '10000');
INSERT INTO propertystring VALUES (8, '1');
INSERT INTO propertystring VALUES (10100, 'english-moderate-stemming');
INSERT INTO propertystring VALUES (10101, 'BDQV-032C-1QG7-0F8S');
INSERT INTO propertystring VALUES (10102, '10300');
INSERT INTO propertystring VALUES (10103, '10316');
INSERT INTO propertystring VALUES (1, '6328');
INSERT INTO propertystring VALUES (13, '6.3');
INSERT INTO propertystring VALUES (14, '6.2.6');
INSERT INTO propertystring VALUES (10202, '');
INSERT INTO propertystring VALUES (10203, '1415910083897');
INSERT INTO propertystring VALUES (10204, 'UTF-8');
INSERT INTO propertystring VALUES (10205, 'Your Company JIRA');
INSERT INTO propertystring VALUES (10206, 'http://localhost:8080');
INSERT INTO propertystring VALUES (10207, 'public');
INSERT INTO propertystring VALUES (10213, 'true');
INSERT INTO propertystring VALUES (10216, 'enterprise');
INSERT INTO propertystring VALUES (10217, 'GNCk9ouuz1vNJ+0VpgebFzM3I9o0mFDEolHfpmsrf8E=');
INSERT INTO propertystring VALUES (10220, '1');
INSERT INTO propertystring VALUES (10222, '2');
INSERT INTO propertystring VALUES (10224, 'true');
INSERT INTO propertystring VALUES (10229, 'show');
INSERT INTO propertystring VALUES (10231, '1');
INSERT INTO propertystring VALUES (10232, '1');
INSERT INTO propertystring VALUES (10233, '1');
INSERT INTO propertystring VALUES (10235, '2');
INSERT INTO propertystring VALUES (10237, '2');
INSERT INTO propertystring VALUES (10238, 'false');
INSERT INTO propertystring VALUES (10239, '1');
INSERT INTO propertystring VALUES (10241, '2001');
INSERT INTO propertystring VALUES (10242, '1');
INSERT INTO propertystring VALUES (10243, '1');
INSERT INTO propertystring VALUES (10247, '#java.util.List
');
INSERT INTO propertystring VALUES (10249, '#java.util.List
');
INSERT INTO propertystring VALUES (10251, '#java.util.List
');
INSERT INTO propertystring VALUES (10252, '#java.util.List
');
INSERT INTO propertystring VALUES (10254, '#java.util.List
');
INSERT INTO propertystring VALUES (10255, '#java.util.List
');
INSERT INTO propertystring VALUES (10256, '#java.util.List
');
INSERT INTO propertystring VALUES (10257, '#java.util.List
');
INSERT INTO propertystring VALUES (10258, '#java.util.List
');
INSERT INTO propertystring VALUES (10261, '4');
INSERT INTO propertystring VALUES (10262, '1');
INSERT INTO propertystring VALUES (10264, 'true');
INSERT INTO propertystring VALUES (10266, '4');
INSERT INTO propertystring VALUES (10269, '2');
INSERT INTO propertystring VALUES (10280, 'https://bitbucket.org');
INSERT INTO propertystring VALUES (10281, 'https://github.com');
INSERT INTO propertystring VALUES (10284, '13');
INSERT INTO propertystring VALUES (10285, '2');
INSERT INTO propertystring VALUES (10286, 'false');
INSERT INTO propertystring VALUES (10287, 'false');
INSERT INTO propertystring VALUES (10288, '/favicon.ico');
INSERT INTO propertystring VALUES (10289, '/images/64jira.png');
INSERT INTO propertystring VALUES (10290, '64');
INSERT INTO propertystring VALUES (10291, '64');
INSERT INTO propertystring VALUES (10292, '/favicon.ico');
INSERT INTO propertystring VALUES (10293, '/images/64jira.png');
INSERT INTO propertystring VALUES (10200, '4');
INSERT INTO propertystring VALUES (10294, '1');
INSERT INTO propertystring VALUES (10296, '3');
INSERT INTO propertystring VALUES (10297, '1');
INSERT INTO propertystring VALUES (10298, '6086');
INSERT INTO propertystring VALUES (10299, 'migrated');
INSERT INTO propertystring VALUES (10300, '#java.util.List
');
INSERT INTO propertystring VALUES (10301, '#java.util.List
');
INSERT INTO propertystring VALUES (10302, '#java.util.List
');
INSERT INTO propertystring VALUES (10303, '#java.util.List
');
INSERT INTO propertystring VALUES (10304, '#java.util.List
');
INSERT INTO propertystring VALUES (10305, '#java.util.List
');
INSERT INTO propertystring VALUES (10306, '#java.util.List
{"userKey":"JIRA","date":1415910257265,"i18nKey":"upm.auditLog.upm.startup","entryType":"UPM_STARTUP","params":[]}');
INSERT INTO propertystring VALUES (10210, './data/attachments');
INSERT INTO propertystring VALUES (10214, './export');


--
-- Data for Name: propertytext; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO propertytext VALUES (10, 'http://www.atlassian.com/*
');
INSERT INTO propertytext VALUES (21, '{
    "edgeMap": {
        "1DEDB66F-FE5C-EDFD-54D0-4D19CDC8CECA": {
            "actionId": 5,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1806.5,
                    "y": 434.0
                },
                {
                    "positiveController": null,
                    "x": 1801.0,
                    "y": 115.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",
            "endPoint": {
                "positiveController": null,
                "x": 1801.0,
                "y": 115.0
            },
            "endStepId": 4,
            "id": "1DEDB66F-FE5C-EDFD-54D0-4D19CDC8CECA",
            "label": "Resolve Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1776.85,
                "y": 355.25
            },
            "lineType": "straight",
            "startNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",
            "startPoint": {
                "positiveController": null,
                "x": 1806.5,
                "y": 434.0
            },
            "startStepId": 5
        },
        "3DF7CEC8-9FBC-C0D0-AFB1-4D19CE6EA230": {
            "actionId": 2,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1469.5,
                    "y": 113.0
                },
                {
                    "positiveController": null,
                    "x": 1614.0,
                    "y": 226.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",
            "endPoint": {
                "positiveController": null,
                "x": 1614.0,
                "y": 226.0
            },
            "endStepId": 6,
            "id": "3DF7CEC8-9FBC-C0D0-AFB1-4D19CE6EA230",
            "label": "Close Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1492.25,
                "y": 154.25
            },
            "lineType": "straight",
            "startNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",
            "startPoint": {
                "positiveController": null,
                "x": 1469.5,
                "y": 113.0
            },
            "startStepId": 1
        },
        "483797F1-1BF4-5E0F-86C6-4D19CE6023A2": {
            "actionId": 5,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1469.5,
                    "y": 113.0
                },
                {
                    "positiveController": null,
                    "x": 1763.0,
                    "y": 113.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",
            "endPoint": {
                "positiveController": null,
                "x": 1763.0,
                "y": 113.0
            },
            "endStepId": 4,
            "id": "483797F1-1BF4-5E0F-86C6-4D19CE6023A2",
            "label": "Resolve Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1551.0,
                "y": 104.0
            },
            "lineType": "straight",
            "startNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",
            "startPoint": {
                "positiveController": null,
                "x": 1469.5,
                "y": 113.0
            },
            "startStepId": 1
        },
        "517D7F32-20FB-309E-8639-4D19CE2ACB54": {
            "actionId": 5,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1434.0,
                    "y": 435.0
                },
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1435.0,
                    "y": 490.0
                },
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1947.0,
                    "y": 494.0
                },
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1950.0,
                    "y": 118.0
                },
                {
                    "positiveController": null,
                    "x": 1763.0,
                    "y": 113.0
                }
            ],
            "controlPoints": [
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1435.0,
                    "y": 490.0
                },
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1947.0,
                    "y": 494.0
                },
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1950.0,
                    "y": 118.0
                }
            ],
            "endNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",
            "endPoint": {
                "positiveController": null,
                "x": 1763.0,
                "y": 113.0
            },
            "endStepId": 4,
            "id": "517D7F32-20FB-309E-8639-4D19CE2ACB54",
            "label": "Resolve Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1631.25,
                "y": 479.5
            },
            "lineType": "poly",
            "startNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",
            "startPoint": {
                "positiveController": null,
                "x": 1434.0,
                "y": 435.0
            },
            "startStepId": 3
        },
        "58BD4605-5FB9-84EA-6952-4D19CE7B454B": {
            "actionId": 1,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1470.0,
                    "y": 16.0
                },
                {
                    "positiveController": null,
                    "x": 1469.5,
                    "y": 113.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",
            "endPoint": {
                "positiveController": null,
                "x": 1469.5,
                "y": 113.0
            },
            "endStepId": 1,
            "id": "58BD4605-5FB9-84EA-6952-4D19CE7B454B",
            "label": "Create Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1475.5,
                "y": 48.5
            },
            "lineType": "straight",
            "startNodeId": "15174530-AE75-04E0-1D9D-4D19CD200835",
            "startPoint": {
                "positiveController": null,
                "x": 1470.0,
                "y": 16.0
            },
            "startStepId": 1
        },
        "92D3DEFD-13AC-06A7-E5D8-4D19CE537791": {
            "actionId": 4,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1439.5,
                    "y": 116.0
                },
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1393.0,
                    "y": 116.0
                },
                {
                    "positiveController": null,
                    "x": 1390.0,
                    "y": 434.0
                }
            ],
            "controlPoints": [
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1393.0,
                    "y": 116.0
                }
            ],
            "endNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",
            "endPoint": {
                "positiveController": null,
                "x": 1390.0,
                "y": 434.0
            },
            "endStepId": 3,
            "id": "92D3DEFD-13AC-06A7-E5D8-4D19CE537791",
            "label": "Start Progress",
            "labelPoint": {
                "positiveController": null,
                "x": 1323.65,
                "y": 193.75
            },
            "lineType": "poly",
            "startNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",
            "startPoint": {
                "positiveController": null,
                "x": 1439.5,
                "y": 116.0
            },
            "startStepId": 1
        },
        "C049EE11-C5BB-F93B-36C3-4D19CDF12B8F": {
            "actionId": 3,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1677.0,
                    "y": 227.0
                },
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1767.05,
                    "y": 230.05
                },
                {
                    "positiveController": null,
                    "x": 1773.5,
                    "y": 425.0
                }
            ],
            "controlPoints": [
                {
                    "positiveController": {
                        "positiveController": null,
                        "x": 0.0,
                        "y": 0.0
                    },
                    "x": 1767.05,
                    "y": 230.05
                }
            ],
            "endNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",
            "endPoint": {
                "positiveController": null,
                "x": 1773.5,
                "y": 425.0
            },
            "endStepId": 5,
            "id": "C049EE11-C5BB-F93B-36C3-4D19CDF12B8F",
            "label": "Reopen Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1703.85,
                "y": 218.5
            },
            "lineType": "poly",
            "startNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",
            "startPoint": {
                "positiveController": null,
                "x": 1677.0,
                "y": 227.0
            },
            "startStepId": 6
        },
        "C9EA1792-2332-8B56-A04D-4D19CD725367": {
            "actionId": 301,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1465.0,
                    "y": 436.0
                },
                {
                    "positiveController": null,
                    "x": 1469.5,
                    "y": 113.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",
            "endPoint": {
                "positiveController": null,
                "x": 1469.5,
                "y": 113.0
            },
            "endStepId": 1,
            "id": "C9EA1792-2332-8B56-A04D-4D19CD725367",
            "label": "Stop Progress",
            "labelPoint": {
                "positiveController": null,
                "x": 1407.8,
                "y": 308.5
            },
            "lineType": "straight",
            "startNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",
            "startPoint": {
                "positiveController": null,
                "x": 1465.0,
                "y": 436.0
            },
            "startStepId": 3
        },
        "CAF37138-6321-E03A-8E41-4D19CDD7DC78": {
            "actionId": 2,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1764.5,
                    "y": 430.0
                },
                {
                    "positiveController": null,
                    "x": 1614.0,
                    "y": 226.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",
            "endPoint": {
                "positiveController": null,
                "x": 1614.0,
                "y": 226.0
            },
            "endStepId": 6,
            "id": "CAF37138-6321-E03A-8E41-4D19CDD7DC78",
            "label": "Close Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1677.65,
                "y": 365.0
            },
            "lineType": "straight",
            "startNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",
            "startPoint": {
                "positiveController": null,
                "x": 1764.5,
                "y": 430.0
            },
            "startStepId": 5
        },
        "E1F8462A-8B0A-87EA-4F70-4D19CE423C83": {
            "actionId": 2,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1488.0,
                    "y": 430.0
                },
                {
                    "positiveController": null,
                    "x": 1614.0,
                    "y": 226.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",
            "endPoint": {
                "positiveController": null,
                "x": 1614.0,
                "y": 226.0
            },
            "endStepId": 6,
            "id": "E1F8462A-8B0A-87EA-4F70-4D19CE423C83",
            "label": "Close Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1492.0,
                "y": 345.0
            },
            "lineType": "straight",
            "startNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",
            "startPoint": {
                "positiveController": null,
                "x": 1488.0,
                "y": 430.0
            },
            "startStepId": 3
        },
        "E27D8EB8-8E49-430B-8FCB-4D19CE127171": {
            "actionId": 3,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1840.0,
                    "y": 130.0
                },
                {
                    "positiveController": null,
                    "x": 1846.5,
                    "y": 428.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",
            "endPoint": {
                "positiveController": null,
                "x": 1846.5,
                "y": 428.0
            },
            "endStepId": 5,
            "id": "E27D8EB8-8E49-430B-8FCB-4D19CE127171",
            "label": "Reopen Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1814.05,
                "y": 169.5
            },
            "lineType": "straight",
            "startNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",
            "startPoint": {
                "positiveController": null,
                "x": 1840.0,
                "y": 130.0
            },
            "startStepId": 4
        },
        "F79E742D-A9E4-0124-D7D4-4D19CDE48C9C": {
            "actionId": 4,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1806.5,
                    "y": 434.0
                },
                {
                    "positiveController": null,
                    "x": 1434.0,
                    "y": 435.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",
            "endPoint": {
                "positiveController": null,
                "x": 1434.0,
                "y": 435.0
            },
            "endStepId": 3,
            "id": "F79E742D-A9E4-0124-D7D4-4D19CDE48C9C",
            "label": "Start Progress",
            "labelPoint": {
                "positiveController": null,
                "x": 1607.25,
                "y": 423.5
            },
            "lineType": "straight",
            "startNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",
            "startPoint": {
                "positiveController": null,
                "x": 1806.5,
                "y": 434.0
            },
            "startStepId": 5
        },
        "FD6BA267-475B-70B3-8AA4-4D19CE00BCD1": {
            "actionId": 701,
            "allPoints": [
                {
                    "positiveController": null,
                    "x": 1763.0,
                    "y": 113.0
                },
                {
                    "positiveController": null,
                    "x": 1614.0,
                    "y": 226.0
                }
            ],
            "controlPoints": [],
            "endNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",
            "endPoint": {
                "positiveController": null,
                "x": 1614.0,
                "y": 226.0
            },
            "endStepId": 6,
            "id": "FD6BA267-475B-70B3-8AA4-4D19CE00BCD1",
            "label": "Close Issue",
            "labelPoint": {
                "positiveController": null,
                "x": 1635.75,
                "y": 152.25
            },
            "lineType": "straight",
            "startNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",
            "startPoint": {
                "positiveController": null,
                "x": 1763.0,
                "y": 113.0
            },
            "startStepId": 4
        }
    },
    "nodeMap": {
        "0740FFFA-2AA1-C90A-38ED-4D19CD61899B": {
            "id": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",
            "inLinkIds": [
                "F79E742D-A9E4-0124-D7D4-4D19CDE48C9C",
                "92D3DEFD-13AC-06A7-E5D8-4D19CE537791"
            ],
            "isInitialAction": false,
            "label": "In Progress",
            "outLinkIds": [
                "C9EA1792-2332-8B56-A04D-4D19CD725367",
                "517D7F32-20FB-309E-8639-4D19CE2ACB54",
                "E1F8462A-8B0A-87EA-4F70-4D19CE423C83"
            ],
            "rect": {
                "height": 45.0,
                "positiveController": null,
                "width": 146.0,
                "x": 1373.0,
                "y": 419.0
            },
            "stepId": 3
        },
        "15174530-AE75-04E0-1D9D-4D19CD200835": {
            "id": "15174530-AE75-04E0-1D9D-4D19CD200835",
            "inLinkIds": [],
            "isInitialAction": true,
            "label": "Create Issue",
            "outLinkIds": [
                "58BD4605-5FB9-84EA-6952-4D19CE7B454B"
            ],
            "rect": {
                "height": 45.0,
                "positiveController": null,
                "width": 157.0,
                "x": 1405.0,
                "y": 0.0
            },
            "stepId": 1
        },
        "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34": {
            "id": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",
            "inLinkIds": [
                "CAF37138-6321-E03A-8E41-4D19CDD7DC78",
                "E1F8462A-8B0A-87EA-4F70-4D19CE423C83",
                "FD6BA267-475B-70B3-8AA4-4D19CE00BCD1",
                "3DF7CEC8-9FBC-C0D0-AFB1-4D19CE6EA230"
            ],
            "isInitialAction": false,
            "label": "Closed",
            "outLinkIds": [
                "C049EE11-C5BB-F93B-36C3-4D19CDF12B8F"
            ],
            "rect": {
                "height": 45.0,
                "positiveController": null,
                "width": 120.0,
                "x": 1569.0,
                "y": 210.0
            },
            "stepId": 6
        },
        "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D": {
            "id": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",
            "inLinkIds": [
                "517D7F32-20FB-309E-8639-4D19CE2ACB54",
                "1DEDB66F-FE5C-EDFD-54D0-4D19CDC8CECA",
                "483797F1-1BF4-5E0F-86C6-4D19CE6023A2"
            ],
            "isInitialAction": false,
            "label": "Resolved",
            "outLinkIds": [
                "FD6BA267-475B-70B3-8AA4-4D19CE00BCD1",
                "E27D8EB8-8E49-430B-8FCB-4D19CE127171"
            ],
            "rect": {
                "height": 44.0,
                "positiveController": null,
                "width": 137.0,
                "x": 1709.0,
                "y": 97.0
            },
            "stepId": 4
        },
        "778534F4-7595-88B6-45E1-4D19CD518712": {
            "id": "778534F4-7595-88B6-45E1-4D19CD518712",
            "inLinkIds": [
                "C9EA1792-2332-8B56-A04D-4D19CD725367",
                "58BD4605-5FB9-84EA-6952-4D19CE7B454B"
            ],
            "isInitialAction": false,
            "label": "Open",
            "outLinkIds": [
                "92D3DEFD-13AC-06A7-E5D8-4D19CE537791",
                "483797F1-1BF4-5E0F-86C6-4D19CE6023A2",
                "3DF7CEC8-9FBC-C0D0-AFB1-4D19CE6EA230"
            ],
            "rect": {
                "height": 45.0,
                "positiveController": null,
                "width": 106.0,
                "x": 1429.5,
                "y": 97.0
            },
            "stepId": 1
        },
        "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB": {
            "id": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",
            "inLinkIds": [
                "E27D8EB8-8E49-430B-8FCB-4D19CE127171",
                "C049EE11-C5BB-F93B-36C3-4D19CDF12B8F"
            ],
            "isInitialAction": false,
            "label": "Reopened",
            "outLinkIds": [
                "1DEDB66F-FE5C-EDFD-54D0-4D19CDC8CECA",
                "CAF37138-6321-E03A-8E41-4D19CDD7DC78",
                "F79E742D-A9E4-0124-D7D4-4D19CDE48C9C"
            ],
            "rect": {
                "height": 45.0,
                "positiveController": null,
                "width": 142.0,
                "x": 1749.5,
                "y": 418.0
            },
            "stepId": 5
        }
    },
    "rootIds": [
        "15174530-AE75-04E0-1D9D-4D19CD200835"
    ],
    "width": 1136
}
');
INSERT INTO propertytext VALUES (10215, 'AAABgw0ODAoPeNp1kkFPwkAQhe/9FZt4LmmrYiTpAegiIAKBgoZ42W4HWF12m9kW5d9bC41tg9e3b958M7M3AxRknEniuMT1Oo7X8dqELkPiOa5rRewQad2aCA7KAI1FKrTy6TSki/litKTWVpg9nKB0hKcE/EGu0RO8d0gAR5A6AbS41EfAmq1fSDXXNDtEgLPtygAa/97iWm1bjKfiCH6KGVjzDPmeGQhYCr7nuq7ttG3nwarkTtkB/ICu6WQ2p4vyhX4nAk9F2fx2WM5VjV4C5jijwO89PYb222p9Zz9vNkO757iv1odAVoMfjxZdQlUKmKAwjVl/qWuT5oLMQPErvnLkvsxMnjbVMRjfaSy+SOkV0n9Nq4RX7sQx4yKS9UP1L2It6IWJvINiOW1jaRz1V9wIyJVa9eU1X+NKfSr9pawZ7pgShhVE3VQyYwRTf0DVG/QRCl/zvOfOVWf576paAIajSIpGIZiUyDMM2Wokicx2QpG4JDXnjVXrL3+0Kv0AGd4bNDAsAhRW+KkhTg9ACxaro+gIxxowDSCtIgIUIWzKR3uE3+3rtlKrTA0zc/5vUtw=X02iq');
INSERT INTO propertytext VALUES (10307, '{"AO_E8B6CC_ISSUE_TO_CHANGESET":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_MESSAGE_TAG":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_563AEE_ACTIVITY_ENTITY":{"key":"com.atlassian.streams.streams-thirdparty-plugin","name":"Streams Third Party Provider Plugin","version":"5.4.1","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_21D670_WHITELIST_RULES":{"key":"com.atlassian.plugins.atlassian-whitelist-api-plugin","name":"Atlassian Whitelist API Plugin","version":"1.7","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_4AEACD_WEBHOOK_DAO":{"key":"com.atlassian.jira.plugins.webhooks.jira-webhooks-plugin","name":"JIRA WebHooks Plugin","version":"1.2.6","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_SYNC_EVENT":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_BRANCH_HEAD_MAPPING":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_563AEE_OBJECT_ENTITY":{"key":"com.atlassian.streams.streams-thirdparty-plugin","name":"Streams Third Party Provider Plugin","version":"5.4.1","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_563AEE_ACTOR_ENTITY":{"key":"com.atlassian.streams.streams-thirdparty-plugin","name":"Streams Third Party Provider Plugin","version":"5.4.1","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_BRANCH":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_SYNC_AUDIT_LOG":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_PR_PARTICIPANT":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_MESSAGE":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_REPOSITORY_MAPPING":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_PULL_REQUEST":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_MESSAGE_QUEUE_ITEM":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_PR_ISSUE_KEY":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_B9A0F0_APPLIED_TEMPLATE":{"key":"com.atlassian.jira.project-templates-plugin","name":"Project Templates Plugin","version":"2.41","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_GIT_HUB_EVENT":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_REPO_TO_CHANGESET":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_563AEE_TARGET_ENTITY":{"key":"com.atlassian.streams.streams-thirdparty-plugin","name":"Streams Third Party Provider Plugin","version":"5.4.1","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_563AEE_MEDIA_LINK_ENTITY":{"key":"com.atlassian.streams.streams-thirdparty-plugin","name":"Streams Third Party Provider Plugin","version":"5.4.1","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_ISSUE_TO_BRANCH":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_ORGANIZATION_MAPPING":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_PR_TO_COMMIT":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_COMMIT":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_E8B6CC_CHANGESET_MAPPING":{"key":"com.atlassian.jira.plugins.jira-bitbucket-connector-plugin","name":"JIRA DVCS Connector Plugin","version":"2.1.15","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"},"AO_563AEE_ACTIVITY_OBJ_ENTITY":{"key":"com.atlassian.streams.streams-thirdparty-plugin","name":"Streams Third Party Provider Plugin","version":"5.4.1","vendorName":"Atlassian","vendorUrl":"http://www.atlassian.com/"}}');
INSERT INTO propertytext VALUES (10308, '#java.util.List
com.atlassian.jira.plugins.jira-importers-bitbucket-plugin
com.atlassian.labs.hipchat.hipchat-for-jira-plugin
com.atlassian.jira.plugins.jira-importers-github-plugin
com.atlassian.jira.plugins.jira-importers-plugin
com.atlassian.jira.extra.jira-ical-feed
com.atlassian.jira.plugins.jira-importers-redmine-plugin');


--
-- Data for Name: qrtz_calendars; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_cron_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_fired_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_job_details; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_job_listeners; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_simple_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_trigger_listeners; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: remembermetoken; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: remotelink; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: replicatedindexoperation; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: resolution; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO resolution VALUES ('1', 1, 'Fixed', 'A fix for this issue is checked into the tree and tested.', NULL);
INSERT INTO resolution VALUES ('2', 2, 'Won''t Fix', 'The problem described is an issue which will never be fixed.', NULL);
INSERT INTO resolution VALUES ('3', 3, 'Duplicate', 'The problem is a duplicate of an existing issue.', NULL);
INSERT INTO resolution VALUES ('4', 4, 'Incomplete', 'The problem is not completely described.', NULL);
INSERT INTO resolution VALUES ('5', 5, 'Cannot Reproduce', 'All attempts at reproducing this issue failed, or not enough information was available to reproduce the issue. Reading the code produces no clues as to why this behavior would occur. If more information appears later, please reopen the issue.', NULL);


--
-- Data for Name: rundetails; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO rundetails VALUES (10001, 'com.atlassian.jira.service.JiraService:10002', '2014-11-14 03:21:23.991+07', 19, 'S', '');
INSERT INTO rundetails VALUES (10002, 'com.atlassian.jira.service.JiraService:10001', '2014-11-14 03:21:23.953+07', 721, 'S', '');
INSERT INTO rundetails VALUES (10006, 'CompatibilityPluginScheduler.JobId.RemotePluginLicenseNotificationJob-job', '2014-11-14 03:24:17.241+07', 29, 'S', '');
INSERT INTO rundetails VALUES (10007, 'CompatibilityPluginScheduler.JobId.PluginRequestCheckJob-job', '2014-11-14 03:24:17.273+07', 44, 'S', '');
INSERT INTO rundetails VALUES (10009, 'CompatibilityPluginScheduler.JobId.LocalPluginLicenseNotificationJob-job', '2014-11-14 03:24:17.222+07', 155, 'S', '');
INSERT INTO rundetails VALUES (10012, 'CompatibilityPluginScheduler.JobId.com.atlassian.jira.plugins.dvcs.scheduler.DvcsScheduler:job', '2014-11-14 03:25:31.811+07', 7, 'S', '');
INSERT INTO rundetails VALUES (10019, 'com.atlassian.jira.service.JiraService:10000', '2014-11-14 03:29:10.507+07', 2, 'S', '');
INSERT INTO rundetails VALUES (10020, 'JiraPluginScheduler:com.atlassian.jira.plugin.ext.bamboo.service.PlanStatusUpdateServiceImpl:job', '2014-11-14 03:29:17.354+07', 2, 'S', '');


--
-- Data for Name: schemeissuesecurities; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: schemeissuesecuritylevels; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: schemepermissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO schemepermissions VALUES (10000, NULL, 0, 'group', 'jira-administrators', NULL);
INSERT INTO schemepermissions VALUES (10001, NULL, 1, 'group', 'jira-users', NULL);
INSERT INTO schemepermissions VALUES (10002, NULL, 27, 'group', 'jira-developers', NULL);
INSERT INTO schemepermissions VALUES (10003, NULL, 24, 'group', 'jira-developers', NULL);
INSERT INTO schemepermissions VALUES (10032, NULL, 22, 'group', 'jira-users', NULL);
INSERT INTO schemepermissions VALUES (10100, NULL, 33, 'group', 'jira-users', NULL);
INSERT INTO schemepermissions VALUES (10101, NULL, 44, 'group', 'jira-administrators', NULL);
INSERT INTO schemepermissions VALUES (10004, 0, 23, 'projectrole', '10002', 'ADMINISTER_PROJECTS');
INSERT INTO schemepermissions VALUES (10005, 0, 10, 'projectrole', '10000', 'BROWSE_PROJECTS');
INSERT INTO schemepermissions VALUES (10006, 0, 11, 'projectrole', '10000', 'CREATE_ISSUES');
INSERT INTO schemepermissions VALUES (10007, 0, 15, 'projectrole', '10000', 'ADD_COMMENTS');
INSERT INTO schemepermissions VALUES (10008, 0, 19, 'projectrole', '10000', 'CREATE_ATTACHMENTS');
INSERT INTO schemepermissions VALUES (10009, 0, 13, 'projectrole', '10001', 'ASSIGN_ISSUES');
INSERT INTO schemepermissions VALUES (10010, 0, 17, 'projectrole', '10001', 'ASSIGNABLE_USER');
INSERT INTO schemepermissions VALUES (10011, 0, 14, 'projectrole', '10001', 'RESOLVE_ISSUES');
INSERT INTO schemepermissions VALUES (10012, 0, 21, 'projectrole', '10000', 'LINK_ISSUES');
INSERT INTO schemepermissions VALUES (10013, 0, 12, 'projectrole', '10001', 'EDIT_ISSUES');
INSERT INTO schemepermissions VALUES (10014, 0, 16, 'projectrole', '10002', 'DELETE_ISSUES');
INSERT INTO schemepermissions VALUES (10015, 0, 18, 'projectrole', '10001', 'CLOSE_ISSUES');
INSERT INTO schemepermissions VALUES (10016, 0, 25, 'projectrole', '10001', 'MOVE_ISSUES');
INSERT INTO schemepermissions VALUES (10017, 0, 28, 'projectrole', '10001', 'SCHEDULE_ISSUES');
INSERT INTO schemepermissions VALUES (10018, 0, 30, 'projectrole', '10002', 'MODIFY_REPORTER');
INSERT INTO schemepermissions VALUES (10019, 0, 20, 'projectrole', '10001', 'WORK_ON_ISSUES');
INSERT INTO schemepermissions VALUES (10020, 0, 43, 'projectrole', '10002', 'DELETE_ALL_WORKLOGS');
INSERT INTO schemepermissions VALUES (10021, 0, 42, 'projectrole', '10000', 'DELETE_OWN_WORKLOGS');
INSERT INTO schemepermissions VALUES (10022, 0, 41, 'projectrole', '10001', 'EDIT_ALL_WORKLOGS');
INSERT INTO schemepermissions VALUES (10023, 0, 40, 'projectrole', '10000', 'EDIT_OWN_WORKLOGS');
INSERT INTO schemepermissions VALUES (10024, 0, 31, 'projectrole', '10001', 'VIEW_VOTERS_AND_WATCHERS');
INSERT INTO schemepermissions VALUES (10025, 0, 32, 'projectrole', '10002', 'MANAGE_WATCHERS');
INSERT INTO schemepermissions VALUES (10026, 0, 34, 'projectrole', '10001', 'EDIT_ALL_COMMENTS');
INSERT INTO schemepermissions VALUES (10027, 0, 35, 'projectrole', '10000', 'EDIT_OWN_COMMENTS');
INSERT INTO schemepermissions VALUES (10028, 0, 36, 'projectrole', '10002', 'DELETE_ALL_COMMENTS');
INSERT INTO schemepermissions VALUES (10029, 0, 37, 'projectrole', '10000', 'DELETE_OWN_COMMENTS');
INSERT INTO schemepermissions VALUES (10030, 0, 38, 'projectrole', '10002', 'DELETE_ALL_ATTACHMENTS');
INSERT INTO schemepermissions VALUES (10031, 0, 39, 'projectrole', '10000', 'DELETE_OWN_ATTACHMENTS');
INSERT INTO schemepermissions VALUES (10033, 0, 29, 'projectrole', '10001', 'VIEW_DEV_TOOLS');
INSERT INTO schemepermissions VALUES (10200, 0, 45, 'projectrole', '10000', 'VIEW_READONLY_WORKFLOW');
INSERT INTO schemepermissions VALUES (10300, 0, 46, 'projectrole', '10001', 'TRANSITION_ISSUES');


--
-- Data for Name: searchrequest; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: sequence_value_item; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO sequence_value_item VALUES ('Group', 10010);
INSERT INTO sequence_value_item VALUES ('IssueType', 10000);
INSERT INTO sequence_value_item VALUES ('IssueLinkType', 10200);
INSERT INTO sequence_value_item VALUES ('IssueTypeScreenSchemeEntity', 10100);
INSERT INTO sequence_value_item VALUES ('Notification', 10200);
INSERT INTO sequence_value_item VALUES ('NotificationScheme', 10100);
INSERT INTO sequence_value_item VALUES ('OAuthConsumer', 10100);
INSERT INTO sequence_value_item VALUES ('OptionConfiguration', 10200);
INSERT INTO sequence_value_item VALUES ('PortalPage', 10100);
INSERT INTO sequence_value_item VALUES ('PortletConfiguration', 10100);
INSERT INTO sequence_value_item VALUES ('Priority', 10000);
INSERT INTO sequence_value_item VALUES ('ProjectRole', 10100);
INSERT INTO sequence_value_item VALUES ('ProjectRoleActor', 10100);
INSERT INTO sequence_value_item VALUES ('Resolution', 10000);
INSERT INTO sequence_value_item VALUES ('SchemePermissions', 10400);
INSERT INTO sequence_value_item VALUES ('ServiceConfig', 10200);
INSERT INTO sequence_value_item VALUES ('SharePermissions', 10100);
INSERT INTO sequence_value_item VALUES ('Status', 10000);
INSERT INTO sequence_value_item VALUES ('Workflow', 10100);
INSERT INTO sequence_value_item VALUES ('WorkflowScheme', 10100);
INSERT INTO sequence_value_item VALUES ('WorkflowSchemeEntity', 10100);
INSERT INTO sequence_value_item VALUES ('ConfigurationContext', 10100);
INSERT INTO sequence_value_item VALUES ('EventType', 10000);
INSERT INTO sequence_value_item VALUES ('FieldConfigScheme', 10100);
INSERT INTO sequence_value_item VALUES ('FieldConfigSchemeIssueType', 10200);
INSERT INTO sequence_value_item VALUES ('FieldConfiguration', 10100);
INSERT INTO sequence_value_item VALUES ('FieldLayout', 10100);
INSERT INTO sequence_value_item VALUES ('FieldScreen', 10000);
INSERT INTO sequence_value_item VALUES ('FieldScreenLayoutItem', 10200);
INSERT INTO sequence_value_item VALUES ('FieldScreenScheme', 10000);
INSERT INTO sequence_value_item VALUES ('FieldScreenSchemeItem', 10100);
INSERT INTO sequence_value_item VALUES ('FieldScreenTab', 10100);
INSERT INTO sequence_value_item VALUES ('GadgetUserPreference', 10100);
INSERT INTO sequence_value_item VALUES ('GenericConfiguration', 10100);
INSERT INTO sequence_value_item VALUES ('GlobalPermissionEntry', 10100);
INSERT INTO sequence_value_item VALUES ('Avatar', 10400);
INSERT INTO sequence_value_item VALUES ('UpgradeHistory', 10100);
INSERT INTO sequence_value_item VALUES ('UpgradeVersionHistory', 10100);
INSERT INTO sequence_value_item VALUES ('RunDetails', 10100);
INSERT INTO sequence_value_item VALUES ('ListenerConfig', 10300);
INSERT INTO sequence_value_item VALUES ('PluginVersion', 10200);
INSERT INTO sequence_value_item VALUES ('ApplicationUser', 10100);
INSERT INTO sequence_value_item VALUES ('User', 10100);
INSERT INTO sequence_value_item VALUES ('UserAttribute', 10100);
INSERT INTO sequence_value_item VALUES ('AuditLog', 10100);
INSERT INTO sequence_value_item VALUES ('AuditChangedValue', 10100);
INSERT INTO sequence_value_item VALUES ('Membership', 10100);
INSERT INTO sequence_value_item VALUES ('AuditItem', 10100);
INSERT INTO sequence_value_item VALUES ('FieldLayoutItem', 10200);
INSERT INTO sequence_value_item VALUES ('OSPropertyEntry', 10400);
INSERT INTO sequence_value_item VALUES ('UserHistoryItem', 10100);


--
-- Data for Name: serviceconfig; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO serviceconfig VALUES (10000, 60000, 'com.atlassian.jira.service.services.mail.MailQueueService', 'Mail Queue Service');
INSERT INTO serviceconfig VALUES (10002, 86400000, 'com.atlassian.jira.service.services.auditing.AuditLogCleaningService', 'Audit log cleaning service');
INSERT INTO serviceconfig VALUES (10001, 43200000, 'com.atlassian.jira.service.services.export.ExportService', 'Backup Service');


--
-- Data for Name: sharepermissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO sharepermissions VALUES (10000, 10000, 'PortalPage', 'global', NULL, NULL);


--
-- Data for Name: trackback_ping; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: trustedapp; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: upgradehistory; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO upgradehistory VALUES (10000, 'com.atlassian.jira.upgrade.tasks.UpgradeTask_Build6322', '6328');
INSERT INTO upgradehistory VALUES (10001, 'com.atlassian.jira.upgrade.tasks.UpgradeTask_Build6323', '6328');
INSERT INTO upgradehistory VALUES (10002, 'com.atlassian.jira.upgrade.tasks.UpgradeTask_Build6325', '6328');
INSERT INTO upgradehistory VALUES (10003, 'com.atlassian.jira.upgrade.tasks.UpgradeTask_Build6326', '6328');
INSERT INTO upgradehistory VALUES (10004, 'com.atlassian.jira.upgrade.tasks.UpgradeTask_Build6327', '6328');
INSERT INTO upgradehistory VALUES (10005, 'com.atlassian.jira.upgrade.tasks.UpgradeTask_Build6328', '6328');


--
-- Data for Name: upgradeversionhistory; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO upgradeversionhistory VALUES (10000, '2014-11-14 03:21:23.797+07', '6328', '6.3');


--
-- Data for Name: userassociation; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: userbase; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: userhistoryitem; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO userhistoryitem VALUES (10000, 'Dashboard', '10000', 'admin', 1415910257542, NULL);


--
-- Data for Name: userpickerfilter; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: userpickerfiltergroup; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: userpickerfilterrole; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: versioncontrol; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: votehistory; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: workflowscheme; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO workflowscheme VALUES (10000, 'classic', 'classic');


--
-- Data for Name: workflowschemeentity; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO workflowschemeentity VALUES (10000, 10000, 'classic default workflow', '0');


--
-- Data for Name: worklog; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: AO_21D670_WHITELIST_RULES_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_21D670_WHITELIST_RULES"
    ADD CONSTRAINT "AO_21D670_WHITELIST_RULES_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_4AEACD_WEBHOOK_DAO_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_4AEACD_WEBHOOK_DAO"
    ADD CONSTRAINT "AO_4AEACD_WEBHOOK_DAO_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_563AEE_ACTIVITY_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT "AO_563AEE_ACTIVITY_ENTITY_pkey" PRIMARY KEY ("ACTIVITY_ID");


--
-- Name: AO_563AEE_ACTOR_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_563AEE_ACTOR_ENTITY"
    ADD CONSTRAINT "AO_563AEE_ACTOR_ENTITY_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_563AEE_MEDIA_LINK_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_563AEE_MEDIA_LINK_ENTITY"
    ADD CONSTRAINT "AO_563AEE_MEDIA_LINK_ENTITY_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_563AEE_OBJECT_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_563AEE_OBJECT_ENTITY"
    ADD CONSTRAINT "AO_563AEE_OBJECT_ENTITY_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_563AEE_TARGET_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_563AEE_TARGET_ENTITY"
    ADD CONSTRAINT "AO_563AEE_TARGET_ENTITY_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_B9A0F0_APPLIED_TEMPLATE_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_B9A0F0_APPLIED_TEMPLATE"
    ADD CONSTRAINT "AO_B9A0F0_APPLIED_TEMPLATE_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_BRANCH_HEAD_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_BRANCH_HEAD_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_BRANCH_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_BRANCH"
    ADD CONSTRAINT "AO_E8B6CC_BRANCH_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_CHANGESET_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_CHANGESET_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_CHANGESET_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_COMMIT_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_COMMIT"
    ADD CONSTRAINT "AO_E8B6CC_COMMIT_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_GIT_HUB_EVENT_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_GIT_HUB_EVENT"
    ADD CONSTRAINT "AO_E8B6CC_GIT_HUB_EVENT_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_MAPPING_V2"
    ADD CONSTRAINT "AO_E8B6CC_ISSUE_MAPPING_V2_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_ISSUE_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_ISSUE_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_TO_BRANCH"
    ADD CONSTRAINT "AO_E8B6CC_ISSUE_TO_BRANCH_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_TO_CHANGESET"
    ADD CONSTRAINT "AO_E8B6CC_ISSUE_TO_CHANGESET_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_MESSAGE_QUEUE_ITEM"
    ADD CONSTRAINT "AO_E8B6CC_MESSAGE_QUEUE_ITEM_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_MESSAGE_TAG_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_MESSAGE_TAG"
    ADD CONSTRAINT "AO_E8B6CC_MESSAGE_TAG_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_MESSAGE_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_MESSAGE"
    ADD CONSTRAINT "AO_E8B6CC_MESSAGE_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_ORGANIZATION_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_ORGANIZATION_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_PROJECT_MAPPING_V2"
    ADD CONSTRAINT "AO_E8B6CC_PROJECT_MAPPING_V2_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_PROJECT_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_PROJECT_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_PROJECT_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_PR_ISSUE_KEY_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_PR_ISSUE_KEY"
    ADD CONSTRAINT "AO_E8B6CC_PR_ISSUE_KEY_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_PR_PARTICIPANT_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_PR_PARTICIPANT"
    ADD CONSTRAINT "AO_E8B6CC_PR_PARTICIPANT_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_PR_TO_COMMIT_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_PR_TO_COMMIT"
    ADD CONSTRAINT "AO_E8B6CC_PR_TO_COMMIT_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_PULL_REQUEST_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_PULL_REQUEST"
    ADD CONSTRAINT "AO_E8B6CC_PULL_REQUEST_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_REPOSITORY_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_REPOSITORY_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_REPOSITORY_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_REPO_TO_CHANGESET_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_REPO_TO_CHANGESET"
    ADD CONSTRAINT "AO_E8B6CC_REPO_TO_CHANGESET_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_SYNC_AUDIT_LOG"
    ADD CONSTRAINT "AO_E8B6CC_SYNC_AUDIT_LOG_pkey" PRIMARY KEY ("ID");


--
-- Name: AO_E8B6CC_SYNC_EVENT_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "AO_E8B6CC_SYNC_EVENT"
    ADD CONSTRAINT "AO_E8B6CC_SYNC_EVENT_pkey" PRIMARY KEY ("ID");


--
-- Name: pk_app_user; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY app_user
    ADD CONSTRAINT pk_app_user PRIMARY KEY (id);


--
-- Name: pk_audit_changed_value; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audit_changed_value
    ADD CONSTRAINT pk_audit_changed_value PRIMARY KEY (id);


--
-- Name: pk_audit_item; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audit_item
    ADD CONSTRAINT pk_audit_item PRIMARY KEY (id);


--
-- Name: pk_audit_log; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audit_log
    ADD CONSTRAINT pk_audit_log PRIMARY KEY (id);


--
-- Name: pk_avatar; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY avatar
    ADD CONSTRAINT pk_avatar PRIMARY KEY (id);


--
-- Name: pk_changegroup; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY changegroup
    ADD CONSTRAINT pk_changegroup PRIMARY KEY (id);


--
-- Name: pk_changeitem; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY changeitem
    ADD CONSTRAINT pk_changeitem PRIMARY KEY (id);


--
-- Name: pk_clusterlockstatus; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY clusterlockstatus
    ADD CONSTRAINT pk_clusterlockstatus PRIMARY KEY (id);


--
-- Name: pk_clustermessage; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY clustermessage
    ADD CONSTRAINT pk_clustermessage PRIMARY KEY (id);


--
-- Name: pk_clusternode; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY clusternode
    ADD CONSTRAINT pk_clusternode PRIMARY KEY (node_id);


--
-- Name: pk_clusternodeheartbeat; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY clusternodeheartbeat
    ADD CONSTRAINT pk_clusternodeheartbeat PRIMARY KEY (node_id);


--
-- Name: pk_columnlayout; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY columnlayout
    ADD CONSTRAINT pk_columnlayout PRIMARY KEY (id);


--
-- Name: pk_columnlayoutitem; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY columnlayoutitem
    ADD CONSTRAINT pk_columnlayoutitem PRIMARY KEY (id);


--
-- Name: pk_component; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY component
    ADD CONSTRAINT pk_component PRIMARY KEY (id);


--
-- Name: pk_configurationcontext; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY configurationcontext
    ADD CONSTRAINT pk_configurationcontext PRIMARY KEY (id);


--
-- Name: pk_customfield; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY customfield
    ADD CONSTRAINT pk_customfield PRIMARY KEY (id);


--
-- Name: pk_customfieldoption; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY customfieldoption
    ADD CONSTRAINT pk_customfieldoption PRIMARY KEY (id);


--
-- Name: pk_customfieldvalue; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY customfieldvalue
    ADD CONSTRAINT pk_customfieldvalue PRIMARY KEY (id);


--
-- Name: pk_cwd_application; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_application
    ADD CONSTRAINT pk_cwd_application PRIMARY KEY (id);


--
-- Name: pk_cwd_application_address; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_application_address
    ADD CONSTRAINT pk_cwd_application_address PRIMARY KEY (application_id, remote_address);


--
-- Name: pk_cwd_directory; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_directory
    ADD CONSTRAINT pk_cwd_directory PRIMARY KEY (id);


--
-- Name: pk_cwd_directory_attribute; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_directory_attribute
    ADD CONSTRAINT pk_cwd_directory_attribute PRIMARY KEY (directory_id, attribute_name);


--
-- Name: pk_cwd_directory_operation; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_directory_operation
    ADD CONSTRAINT pk_cwd_directory_operation PRIMARY KEY (directory_id, operation_type);


--
-- Name: pk_cwd_group; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_group
    ADD CONSTRAINT pk_cwd_group PRIMARY KEY (id);


--
-- Name: pk_cwd_group_attributes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_group_attributes
    ADD CONSTRAINT pk_cwd_group_attributes PRIMARY KEY (id);


--
-- Name: pk_cwd_membership; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_membership
    ADD CONSTRAINT pk_cwd_membership PRIMARY KEY (id);


--
-- Name: pk_cwd_user; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_user
    ADD CONSTRAINT pk_cwd_user PRIMARY KEY (id);


--
-- Name: pk_cwd_user_attributes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cwd_user_attributes
    ADD CONSTRAINT pk_cwd_user_attributes PRIMARY KEY (id);


--
-- Name: pk_draftworkflowscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY draftworkflowscheme
    ADD CONSTRAINT pk_draftworkflowscheme PRIMARY KEY (id);


--
-- Name: pk_draftworkflowschemeentity; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY draftworkflowschemeentity
    ADD CONSTRAINT pk_draftworkflowschemeentity PRIMARY KEY (id);


--
-- Name: pk_entity_property; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY entity_property
    ADD CONSTRAINT pk_entity_property PRIMARY KEY (id);


--
-- Name: pk_entity_property_index_docum; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY entity_property_index_document
    ADD CONSTRAINT pk_entity_property_index_docum PRIMARY KEY (id);


--
-- Name: pk_external_entities; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY external_entities
    ADD CONSTRAINT pk_external_entities PRIMARY KEY (id);


--
-- Name: pk_externalgadget; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY externalgadget
    ADD CONSTRAINT pk_externalgadget PRIMARY KEY (id);


--
-- Name: pk_favouriteassociations; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY favouriteassociations
    ADD CONSTRAINT pk_favouriteassociations PRIMARY KEY (id);


--
-- Name: pk_feature; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY feature
    ADD CONSTRAINT pk_feature PRIMARY KEY (id);


--
-- Name: pk_fieldconfigscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldconfigscheme
    ADD CONSTRAINT pk_fieldconfigscheme PRIMARY KEY (id);


--
-- Name: pk_fieldconfigschemeissuetype; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldconfigschemeissuetype
    ADD CONSTRAINT pk_fieldconfigschemeissuetype PRIMARY KEY (id);


--
-- Name: pk_fieldconfiguration; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldconfiguration
    ADD CONSTRAINT pk_fieldconfiguration PRIMARY KEY (id);


--
-- Name: pk_fieldlayout; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldlayout
    ADD CONSTRAINT pk_fieldlayout PRIMARY KEY (id);


--
-- Name: pk_fieldlayoutitem; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldlayoutitem
    ADD CONSTRAINT pk_fieldlayoutitem PRIMARY KEY (id);


--
-- Name: pk_fieldlayoutscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldlayoutscheme
    ADD CONSTRAINT pk_fieldlayoutscheme PRIMARY KEY (id);


--
-- Name: pk_fieldlayoutschemeassociatio; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldlayoutschemeassociation
    ADD CONSTRAINT pk_fieldlayoutschemeassociatio PRIMARY KEY (id);


--
-- Name: pk_fieldlayoutschemeentity; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldlayoutschemeentity
    ADD CONSTRAINT pk_fieldlayoutschemeentity PRIMARY KEY (id);


--
-- Name: pk_fieldscreen; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldscreen
    ADD CONSTRAINT pk_fieldscreen PRIMARY KEY (id);


--
-- Name: pk_fieldscreenlayoutitem; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldscreenlayoutitem
    ADD CONSTRAINT pk_fieldscreenlayoutitem PRIMARY KEY (id);


--
-- Name: pk_fieldscreenscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldscreenscheme
    ADD CONSTRAINT pk_fieldscreenscheme PRIMARY KEY (id);


--
-- Name: pk_fieldscreenschemeitem; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldscreenschemeitem
    ADD CONSTRAINT pk_fieldscreenschemeitem PRIMARY KEY (id);


--
-- Name: pk_fieldscreentab; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fieldscreentab
    ADD CONSTRAINT pk_fieldscreentab PRIMARY KEY (id);


--
-- Name: pk_fileattachment; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fileattachment
    ADD CONSTRAINT pk_fileattachment PRIMARY KEY (id);


--
-- Name: pk_filtersubscription; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY filtersubscription
    ADD CONSTRAINT pk_filtersubscription PRIMARY KEY (id);


--
-- Name: pk_gadgetuserpreference; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gadgetuserpreference
    ADD CONSTRAINT pk_gadgetuserpreference PRIMARY KEY (id);


--
-- Name: pk_genericconfiguration; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY genericconfiguration
    ADD CONSTRAINT pk_genericconfiguration PRIMARY KEY (id);


--
-- Name: pk_globalpermissionentry; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY globalpermissionentry
    ADD CONSTRAINT pk_globalpermissionentry PRIMARY KEY (id);


--
-- Name: pk_groupbase; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY groupbase
    ADD CONSTRAINT pk_groupbase PRIMARY KEY (id);


--
-- Name: pk_issuelink; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY issuelink
    ADD CONSTRAINT pk_issuelink PRIMARY KEY (id);


--
-- Name: pk_issuelinktype; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY issuelinktype
    ADD CONSTRAINT pk_issuelinktype PRIMARY KEY (id);


--
-- Name: pk_issuesecurityscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY issuesecurityscheme
    ADD CONSTRAINT pk_issuesecurityscheme PRIMARY KEY (id);


--
-- Name: pk_issuestatus; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY issuestatus
    ADD CONSTRAINT pk_issuestatus PRIMARY KEY (id);


--
-- Name: pk_issuetype; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY issuetype
    ADD CONSTRAINT pk_issuetype PRIMARY KEY (id);


--
-- Name: pk_issuetypescreenscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY issuetypescreenscheme
    ADD CONSTRAINT pk_issuetypescreenscheme PRIMARY KEY (id);


--
-- Name: pk_issuetypescreenschemeentity; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY issuetypescreenschemeentity
    ADD CONSTRAINT pk_issuetypescreenschemeentity PRIMARY KEY (id);


--
-- Name: pk_jiraaction; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jiraaction
    ADD CONSTRAINT pk_jiraaction PRIMARY KEY (id);


--
-- Name: pk_jiradraftworkflows; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jiradraftworkflows
    ADD CONSTRAINT pk_jiradraftworkflows PRIMARY KEY (id);


--
-- Name: pk_jiraeventtype; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jiraeventtype
    ADD CONSTRAINT pk_jiraeventtype PRIMARY KEY (id);


--
-- Name: pk_jiraissue; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jiraissue
    ADD CONSTRAINT pk_jiraissue PRIMARY KEY (id);


--
-- Name: pk_jiraperms; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jiraperms
    ADD CONSTRAINT pk_jiraperms PRIMARY KEY (id);


--
-- Name: pk_jiraworkflows; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jiraworkflows
    ADD CONSTRAINT pk_jiraworkflows PRIMARY KEY (id);


--
-- Name: pk_jquartz_blob_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_blob_triggers
    ADD CONSTRAINT pk_jquartz_blob_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- Name: pk_jquartz_calendars; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_calendars
    ADD CONSTRAINT pk_jquartz_calendars PRIMARY KEY (calendar_name);


--
-- Name: pk_jquartz_cron_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_cron_triggers
    ADD CONSTRAINT pk_jquartz_cron_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- Name: pk_jquartz_fired_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_fired_triggers
    ADD CONSTRAINT pk_jquartz_fired_triggers PRIMARY KEY (entry_id);


--
-- Name: pk_jquartz_job_details; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_job_details
    ADD CONSTRAINT pk_jquartz_job_details PRIMARY KEY (job_name, job_group);


--
-- Name: pk_jquartz_job_listeners; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_job_listeners
    ADD CONSTRAINT pk_jquartz_job_listeners PRIMARY KEY (job_name, job_group, job_listener);


--
-- Name: pk_jquartz_locks; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_locks
    ADD CONSTRAINT pk_jquartz_locks PRIMARY KEY (lock_name);


--
-- Name: pk_jquartz_paused_trigger_grps; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_paused_trigger_grps
    ADD CONSTRAINT pk_jquartz_paused_trigger_grps PRIMARY KEY (trigger_group);


--
-- Name: pk_jquartz_scheduler_state; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_scheduler_state
    ADD CONSTRAINT pk_jquartz_scheduler_state PRIMARY KEY (instance_name);


--
-- Name: pk_jquartz_simple_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_simple_triggers
    ADD CONSTRAINT pk_jquartz_simple_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- Name: pk_jquartz_simprop_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_simprop_triggers
    ADD CONSTRAINT pk_jquartz_simprop_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- Name: pk_jquartz_trigger_listeners; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_trigger_listeners
    ADD CONSTRAINT pk_jquartz_trigger_listeners PRIMARY KEY (trigger_group, trigger_listener);


--
-- Name: pk_jquartz_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jquartz_triggers
    ADD CONSTRAINT pk_jquartz_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- Name: pk_label; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY label
    ADD CONSTRAINT pk_label PRIMARY KEY (id);


--
-- Name: pk_licenserolesgroup; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY licenserolesgroup
    ADD CONSTRAINT pk_licenserolesgroup PRIMARY KEY (id);


--
-- Name: pk_listenerconfig; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY listenerconfig
    ADD CONSTRAINT pk_listenerconfig PRIMARY KEY (id);


--
-- Name: pk_mailserver; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mailserver
    ADD CONSTRAINT pk_mailserver PRIMARY KEY (id);


--
-- Name: pk_managedconfigurationitem; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY managedconfigurationitem
    ADD CONSTRAINT pk_managedconfigurationitem PRIMARY KEY (id);


--
-- Name: pk_membershipbase; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY membershipbase
    ADD CONSTRAINT pk_membershipbase PRIMARY KEY (id);


--
-- Name: pk_moved_issue_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY moved_issue_key
    ADD CONSTRAINT pk_moved_issue_key PRIMARY KEY (id);


--
-- Name: pk_nodeassociation; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY nodeassociation
    ADD CONSTRAINT pk_nodeassociation PRIMARY KEY (source_node_id, source_node_entity, sink_node_id, sink_node_entity, association_type);


--
-- Name: pk_nodeindexcounter; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY nodeindexcounter
    ADD CONSTRAINT pk_nodeindexcounter PRIMARY KEY (id);


--
-- Name: pk_notification; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY notification
    ADD CONSTRAINT pk_notification PRIMARY KEY (id);


--
-- Name: pk_notificationinstance; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY notificationinstance
    ADD CONSTRAINT pk_notificationinstance PRIMARY KEY (id);


--
-- Name: pk_notificationscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY notificationscheme
    ADD CONSTRAINT pk_notificationscheme PRIMARY KEY (id);


--
-- Name: pk_oauthconsumer; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oauthconsumer
    ADD CONSTRAINT pk_oauthconsumer PRIMARY KEY (id);


--
-- Name: pk_oauthconsumertoken; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oauthconsumertoken
    ADD CONSTRAINT pk_oauthconsumertoken PRIMARY KEY (id);


--
-- Name: pk_oauthspconsumer; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oauthspconsumer
    ADD CONSTRAINT pk_oauthspconsumer PRIMARY KEY (id);


--
-- Name: pk_oauthsptoken; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oauthsptoken
    ADD CONSTRAINT pk_oauthsptoken PRIMARY KEY (id);


--
-- Name: pk_optionconfiguration; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY optionconfiguration
    ADD CONSTRAINT pk_optionconfiguration PRIMARY KEY (id);


--
-- Name: pk_os_currentstep; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY os_currentstep
    ADD CONSTRAINT pk_os_currentstep PRIMARY KEY (id);


--
-- Name: pk_os_currentstep_prev; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY os_currentstep_prev
    ADD CONSTRAINT pk_os_currentstep_prev PRIMARY KEY (id, previous_id);


--
-- Name: pk_os_historystep; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY os_historystep
    ADD CONSTRAINT pk_os_historystep PRIMARY KEY (id);


--
-- Name: pk_os_historystep_prev; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY os_historystep_prev
    ADD CONSTRAINT pk_os_historystep_prev PRIMARY KEY (id, previous_id);


--
-- Name: pk_os_wfentry; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY os_wfentry
    ADD CONSTRAINT pk_os_wfentry PRIMARY KEY (id);


--
-- Name: pk_permissionscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY permissionscheme
    ADD CONSTRAINT pk_permissionscheme PRIMARY KEY (id);


--
-- Name: pk_pluginstate; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY pluginstate
    ADD CONSTRAINT pk_pluginstate PRIMARY KEY (pluginkey);


--
-- Name: pk_pluginversion; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY pluginversion
    ADD CONSTRAINT pk_pluginversion PRIMARY KEY (id);


--
-- Name: pk_portalpage; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY portalpage
    ADD CONSTRAINT pk_portalpage PRIMARY KEY (id);


--
-- Name: pk_portletconfiguration; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY portletconfiguration
    ADD CONSTRAINT pk_portletconfiguration PRIMARY KEY (id);


--
-- Name: pk_priority; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY priority
    ADD CONSTRAINT pk_priority PRIMARY KEY (id);


--
-- Name: pk_productlicense; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY productlicense
    ADD CONSTRAINT pk_productlicense PRIMARY KEY (id);


--
-- Name: pk_project; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY project
    ADD CONSTRAINT pk_project PRIMARY KEY (id);


--
-- Name: pk_project_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY project_key
    ADD CONSTRAINT pk_project_key PRIMARY KEY (id);


--
-- Name: pk_projectcategory; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY projectcategory
    ADD CONSTRAINT pk_projectcategory PRIMARY KEY (id);


--
-- Name: pk_projectrole; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY projectrole
    ADD CONSTRAINT pk_projectrole PRIMARY KEY (id);


--
-- Name: pk_projectroleactor; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY projectroleactor
    ADD CONSTRAINT pk_projectroleactor PRIMARY KEY (id);


--
-- Name: pk_projectversion; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY projectversion
    ADD CONSTRAINT pk_projectversion PRIMARY KEY (id);


--
-- Name: pk_propertydata; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY propertydata
    ADD CONSTRAINT pk_propertydata PRIMARY KEY (id);


--
-- Name: pk_propertydate; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY propertydate
    ADD CONSTRAINT pk_propertydate PRIMARY KEY (id);


--
-- Name: pk_propertydecimal; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY propertydecimal
    ADD CONSTRAINT pk_propertydecimal PRIMARY KEY (id);


--
-- Name: pk_propertyentry; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY propertyentry
    ADD CONSTRAINT pk_propertyentry PRIMARY KEY (id);


--
-- Name: pk_propertynumber; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY propertynumber
    ADD CONSTRAINT pk_propertynumber PRIMARY KEY (id);


--
-- Name: pk_propertystring; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY propertystring
    ADD CONSTRAINT pk_propertystring PRIMARY KEY (id);


--
-- Name: pk_propertytext; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY propertytext
    ADD CONSTRAINT pk_propertytext PRIMARY KEY (id);


--
-- Name: pk_qrtz_calendars; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qrtz_calendars
    ADD CONSTRAINT pk_qrtz_calendars PRIMARY KEY (calendar_name);


--
-- Name: pk_qrtz_cron_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qrtz_cron_triggers
    ADD CONSTRAINT pk_qrtz_cron_triggers PRIMARY KEY (id);


--
-- Name: pk_qrtz_fired_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qrtz_fired_triggers
    ADD CONSTRAINT pk_qrtz_fired_triggers PRIMARY KEY (entry_id);


--
-- Name: pk_qrtz_job_details; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qrtz_job_details
    ADD CONSTRAINT pk_qrtz_job_details PRIMARY KEY (id);


--
-- Name: pk_qrtz_job_listeners; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qrtz_job_listeners
    ADD CONSTRAINT pk_qrtz_job_listeners PRIMARY KEY (id);


--
-- Name: pk_qrtz_simple_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qrtz_simple_triggers
    ADD CONSTRAINT pk_qrtz_simple_triggers PRIMARY KEY (id);


--
-- Name: pk_qrtz_trigger_listeners; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qrtz_trigger_listeners
    ADD CONSTRAINT pk_qrtz_trigger_listeners PRIMARY KEY (id);


--
-- Name: pk_qrtz_triggers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qrtz_triggers
    ADD CONSTRAINT pk_qrtz_triggers PRIMARY KEY (id);


--
-- Name: pk_remembermetoken; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY remembermetoken
    ADD CONSTRAINT pk_remembermetoken PRIMARY KEY (id);


--
-- Name: pk_remotelink; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY remotelink
    ADD CONSTRAINT pk_remotelink PRIMARY KEY (id);


--
-- Name: pk_replicatedindexoperation; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY replicatedindexoperation
    ADD CONSTRAINT pk_replicatedindexoperation PRIMARY KEY (id);


--
-- Name: pk_resolution; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY resolution
    ADD CONSTRAINT pk_resolution PRIMARY KEY (id);


--
-- Name: pk_rundetails; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rundetails
    ADD CONSTRAINT pk_rundetails PRIMARY KEY (id);


--
-- Name: pk_schemeissuesecurities; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schemeissuesecurities
    ADD CONSTRAINT pk_schemeissuesecurities PRIMARY KEY (id);


--
-- Name: pk_schemeissuesecuritylevels; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schemeissuesecuritylevels
    ADD CONSTRAINT pk_schemeissuesecuritylevels PRIMARY KEY (id);


--
-- Name: pk_schemepermissions; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schemepermissions
    ADD CONSTRAINT pk_schemepermissions PRIMARY KEY (id);


--
-- Name: pk_searchrequest; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY searchrequest
    ADD CONSTRAINT pk_searchrequest PRIMARY KEY (id);


--
-- Name: pk_sequence_value_item; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sequence_value_item
    ADD CONSTRAINT pk_sequence_value_item PRIMARY KEY (seq_name);


--
-- Name: pk_serviceconfig; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY serviceconfig
    ADD CONSTRAINT pk_serviceconfig PRIMARY KEY (id);


--
-- Name: pk_sharepermissions; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sharepermissions
    ADD CONSTRAINT pk_sharepermissions PRIMARY KEY (id);


--
-- Name: pk_trackback_ping; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY trackback_ping
    ADD CONSTRAINT pk_trackback_ping PRIMARY KEY (id);


--
-- Name: pk_trustedapp; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY trustedapp
    ADD CONSTRAINT pk_trustedapp PRIMARY KEY (id);


--
-- Name: pk_upgradehistory; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY upgradehistory
    ADD CONSTRAINT pk_upgradehistory PRIMARY KEY (upgradeclass);


--
-- Name: pk_upgradeversionhistory; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY upgradeversionhistory
    ADD CONSTRAINT pk_upgradeversionhistory PRIMARY KEY (targetbuild);


--
-- Name: pk_userassociation; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userassociation
    ADD CONSTRAINT pk_userassociation PRIMARY KEY (source_name, sink_node_id, sink_node_entity, association_type);


--
-- Name: pk_userbase; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userbase
    ADD CONSTRAINT pk_userbase PRIMARY KEY (id);


--
-- Name: pk_userhistoryitem; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userhistoryitem
    ADD CONSTRAINT pk_userhistoryitem PRIMARY KEY (id);


--
-- Name: pk_userpickerfilter; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userpickerfilter
    ADD CONSTRAINT pk_userpickerfilter PRIMARY KEY (id);


--
-- Name: pk_userpickerfiltergroup; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userpickerfiltergroup
    ADD CONSTRAINT pk_userpickerfiltergroup PRIMARY KEY (id);


--
-- Name: pk_userpickerfilterrole; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userpickerfilterrole
    ADD CONSTRAINT pk_userpickerfilterrole PRIMARY KEY (id);


--
-- Name: pk_versioncontrol; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY versioncontrol
    ADD CONSTRAINT pk_versioncontrol PRIMARY KEY (id);


--
-- Name: pk_votehistory; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY votehistory
    ADD CONSTRAINT pk_votehistory PRIMARY KEY (id);


--
-- Name: pk_workflowscheme; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY workflowscheme
    ADD CONSTRAINT pk_workflowscheme PRIMARY KEY (id);


--
-- Name: pk_workflowschemeentity; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY workflowschemeentity
    ADD CONSTRAINT pk_workflowschemeentity PRIMARY KEY (id);


--
-- Name: pk_worklog; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY worklog
    ADD CONSTRAINT pk_worklog PRIMARY KEY (id);


--
-- Name: action_authorcreated; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX action_authorcreated ON jiraaction USING btree (issueid, author, created);


--
-- Name: action_authorupdated; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX action_authorupdated ON jiraaction USING btree (issueid, author, updated);


--
-- Name: action_issue; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX action_issue ON jiraaction USING btree (issueid, actiontype);


--
-- Name: attach_issue; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX attach_issue ON fileattachment USING btree (issueid);


--
-- Name: avatar_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX avatar_index ON avatar USING btree (avatartype, owner);


--
-- Name: cf_cfoption; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX cf_cfoption ON customfieldoption USING btree (customfield);


--
-- Name: cf_userpickerfiltergroup; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX cf_userpickerfiltergroup ON userpickerfiltergroup USING btree (userpickerfilter);


--
-- Name: cf_userpickerfilterrole; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX cf_userpickerfilterrole ON userpickerfilterrole USING btree (userpickerfilter);


--
-- Name: cfvalue_issue; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX cfvalue_issue ON customfieldvalue USING btree (issue, customfield);


--
-- Name: chggroup_issue; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX chggroup_issue ON changegroup USING btree (issueid);


--
-- Name: chgitem_chggrp; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX chgitem_chggrp ON changeitem USING btree (groupid);


--
-- Name: chgitem_field; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX chgitem_field ON changeitem USING btree (field);


--
-- Name: cl_searchrequest; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX cl_searchrequest ON columnlayout USING btree (searchrequest);


--
-- Name: cl_username; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX cl_username ON columnlayout USING btree (username);


--
-- Name: cluster_lock_name_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX cluster_lock_name_idx ON clusterlockstatus USING btree (lock_name);


--
-- Name: confcontext; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX confcontext ON configurationcontext USING btree (projectcategory, project, customfield);


--
-- Name: confcontextfieldconfigscheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX confcontextfieldconfigscheme ON configurationcontext USING btree (fieldconfigscheme);


--
-- Name: confcontextprojectkey; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX confcontextprojectkey ON configurationcontext USING btree (project, customfield);


--
-- Name: draft_workflow_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX draft_workflow_scheme ON draftworkflowschemeentity USING btree (scheme);


--
-- Name: draft_workflow_scheme_parent; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX draft_workflow_scheme_parent ON draftworkflowscheme USING btree (workflow_scheme_id);


--
-- Name: entityproperty_identiti; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX entityproperty_identiti ON entity_property USING btree (entity_name, entity_id, property_key);


--
-- Name: entpropindexdoc_module; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX entpropindexdoc_module ON entity_property_index_document USING btree (plugin_key, module_key);


--
-- Name: ext_entity_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ext_entity_name ON external_entities USING btree (name);


--
-- Name: favourite_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX favourite_index ON favouriteassociations USING btree (username, entitytype, entityid);


--
-- Name: fc_fieldid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fc_fieldid ON fieldconfiguration USING btree (fieldid);


--
-- Name: fcs_fieldid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fcs_fieldid ON fieldconfigscheme USING btree (fieldid);


--
-- Name: fcs_issuetype; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fcs_issuetype ON fieldconfigschemeissuetype USING btree (issuetype);


--
-- Name: fcs_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fcs_scheme ON fieldconfigschemeissuetype USING btree (fieldconfigscheme);


--
-- Name: feature_id_userkey; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX feature_id_userkey ON feature USING btree (id, user_key);


--
-- Name: fieldid_fieldconf; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fieldid_fieldconf ON optionconfiguration USING btree (fieldid, fieldconfig);


--
-- Name: fieldid_optionid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fieldid_optionid ON optionconfiguration USING btree (fieldid, optionid);


--
-- Name: fieldlayout_layout; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fieldlayout_layout ON fieldlayoutschemeentity USING btree (fieldlayout);


--
-- Name: fieldlayout_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fieldlayout_scheme ON fieldlayoutschemeentity USING btree (scheme);


--
-- Name: fieldscitem_tab; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fieldscitem_tab ON fieldscreenlayoutitem USING btree (fieldscreentab);


--
-- Name: fieldscreen_field; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fieldscreen_field ON fieldscreenlayoutitem USING btree (fieldidentifier);


--
-- Name: fieldscreen_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fieldscreen_scheme ON issuetypescreenschemeentity USING btree (fieldscreenscheme);


--
-- Name: fieldscreen_tab; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fieldscreen_tab ON fieldscreentab USING btree (fieldscreen);


--
-- Name: fl_scheme_assoc; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fl_scheme_assoc ON fieldlayoutschemeassociation USING btree (project, issuetype);


--
-- Name: historystep_entryid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX historystep_entryid ON os_historystep USING btree (entry_id);


--
-- Name: idx_all_project_ids; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_all_project_ids ON project_key USING btree (project_id);


--
-- Name: idx_all_project_keys; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX idx_all_project_keys ON project_key USING btree (project_key);


--
-- Name: idx_audit_item_log_id2; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_audit_item_log_id2 ON audit_item USING btree (log_id);


--
-- Name: idx_audit_log_created; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_audit_log_created ON audit_log USING btree (created);


--
-- Name: idx_changed_value_log_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_changed_value_log_id ON audit_changed_value USING btree (log_id);


--
-- Name: idx_directory_active; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_directory_active ON cwd_directory USING btree (active);


--
-- Name: idx_directory_impl; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_directory_impl ON cwd_directory USING btree (lower_impl_class);


--
-- Name: idx_directory_type; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_directory_type ON cwd_directory USING btree (directory_type);


--
-- Name: idx_display_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_display_name ON cwd_user USING btree (lower_display_name);


--
-- Name: idx_email_address; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_email_address ON cwd_user USING btree (lower_email_address);


--
-- Name: idx_first_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_first_name ON cwd_user USING btree (lower_first_name);


--
-- Name: idx_group_active; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_group_active ON cwd_group USING btree (lower_group_name, active);


--
-- Name: idx_group_attr_dir_name_lval; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_group_attr_dir_name_lval ON cwd_group_attributes USING btree (directory_id, attribute_name, lower_attribute_value);


--
-- Name: idx_group_dir_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_group_dir_id ON cwd_group USING btree (directory_id);


--
-- Name: idx_last_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_last_name ON cwd_user USING btree (lower_last_name);


--
-- Name: idx_mem_dir_child; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_mem_dir_child ON cwd_membership USING btree (lower_child_name, membership_type, directory_id);


--
-- Name: idx_mem_dir_parent; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_mem_dir_parent ON cwd_membership USING btree (lower_parent_name, membership_type, directory_id);


--
-- Name: idx_mem_dir_parent_child; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_mem_dir_parent_child ON cwd_membership USING btree (lower_parent_name, lower_child_name, membership_type, directory_id);


--
-- Name: idx_old_issue_key; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX idx_old_issue_key ON moved_issue_key USING btree (old_issue_key);


--
-- Name: idx_project_key; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX idx_project_key ON project USING btree (pkey);


--
-- Name: idx_qrtz_ft_inst_job_req_rcvry; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON jquartz_fired_triggers USING btree (sched_name, instance_name, requests_recovery);


--
-- Name: idx_qrtz_ft_j_g; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_ft_j_g ON jquartz_fired_triggers USING btree (sched_name, job_name, job_group);


--
-- Name: idx_qrtz_ft_jg; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_ft_jg ON jquartz_fired_triggers USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_ft_t_g; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_ft_t_g ON jquartz_fired_triggers USING btree (sched_name, trigger_name, trigger_group);


--
-- Name: idx_qrtz_ft_tg; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_ft_tg ON jquartz_fired_triggers USING btree (sched_name, trigger_group);


--
-- Name: idx_qrtz_ft_trig_inst_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_ft_trig_inst_name ON jquartz_fired_triggers USING btree (sched_name, instance_name);


--
-- Name: idx_qrtz_j_g; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_j_g ON jquartz_triggers USING btree (sched_name, trigger_group);


--
-- Name: idx_qrtz_j_grp; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_j_grp ON jquartz_job_details USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_j_req_recovery; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_j_req_recovery ON jquartz_job_details USING btree (sched_name, requests_recovery);


--
-- Name: idx_qrtz_j_state; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_j_state ON jquartz_triggers USING btree (sched_name, trigger_state);


--
-- Name: idx_qrtz_t_c; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_c ON jquartz_triggers USING btree (sched_name, calendar_name);


--
-- Name: idx_qrtz_t_j; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_j ON jquartz_triggers USING btree (sched_name, job_name, job_group);


--
-- Name: idx_qrtz_t_jg; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_jg ON jquartz_triggers USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_t_n_g_state; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_n_g_state ON jquartz_triggers USING btree (sched_name, trigger_group, trigger_state);


--
-- Name: idx_qrtz_t_n_state; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_n_state ON jquartz_triggers USING btree (sched_name, trigger_name, trigger_group, trigger_state);


--
-- Name: idx_qrtz_t_next_fire_time; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_next_fire_time ON jquartz_triggers USING btree (sched_name, next_fire_time);


--
-- Name: idx_qrtz_t_nft_misfire; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_nft_misfire ON jquartz_triggers USING btree (sched_name, misfire_instr, next_fire_time);


--
-- Name: idx_qrtz_t_nft_st; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_nft_st ON jquartz_triggers USING btree (sched_name, trigger_state, next_fire_time);


--
-- Name: idx_qrtz_t_nft_st_misfire; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_nft_st_misfire ON jquartz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_state);


--
-- Name: idx_qrtz_t_nft_st_misfire_grp; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON jquartz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);


--
-- Name: idx_user_attr_dir_name_lval; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_user_attr_dir_name_lval ON cwd_user_attributes USING btree (directory_id, attribute_name, lower_attribute_value);


--
-- Name: index_ao_563aee_act1642652291; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_563aee_act1642652291 ON "AO_563AEE_ACTIVITY_ENTITY" USING btree ("OBJECT_ID");


--
-- Name: index_ao_563aee_act1978295567; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_563aee_act1978295567 ON "AO_563AEE_ACTIVITY_ENTITY" USING btree ("TARGET_ID");


--
-- Name: index_ao_563aee_act972488439; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_563aee_act972488439 ON "AO_563AEE_ACTIVITY_ENTITY" USING btree ("ICON_ID");


--
-- Name: index_ao_563aee_act995325379; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_563aee_act995325379 ON "AO_563AEE_ACTIVITY_ENTITY" USING btree ("ACTOR_ID");


--
-- Name: index_ao_563aee_obj696886343; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_563aee_obj696886343 ON "AO_563AEE_OBJECT_ENTITY" USING btree ("IMAGE_ID");


--
-- Name: index_ao_563aee_tar521440921; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_563aee_tar521440921 ON "AO_563AEE_TARGET_ENTITY" USING btree ("IMAGE_ID");


--
-- Name: index_ao_e8b6cc_bra1368852151; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_bra1368852151 ON "AO_E8B6CC_BRANCH_HEAD_MAPPING" USING btree ("REPOSITORY_ID");


--
-- Name: index_ao_e8b6cc_bra405461593; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_bra405461593 ON "AO_E8B6CC_BRANCH" USING btree ("REPOSITORY_ID");


--
-- Name: index_ao_e8b6cc_git1804640320; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_git1804640320 ON "AO_E8B6CC_GIT_HUB_EVENT" USING btree ("REPOSITORY_ID");


--
-- Name: index_ao_e8b6cc_iss1229805759; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_iss1229805759 ON "AO_E8B6CC_ISSUE_TO_CHANGESET" USING btree ("CHANGESET_ID");


--
-- Name: index_ao_e8b6cc_iss1325927291; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_iss1325927291 ON "AO_E8B6CC_ISSUE_TO_BRANCH" USING btree ("BRANCH_ID");


--
-- Name: index_ao_e8b6cc_mes1391090780; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_mes1391090780 ON "AO_E8B6CC_MESSAGE_TAG" USING btree ("MESSAGE_ID");


--
-- Name: index_ao_e8b6cc_mes344532677; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_mes344532677 ON "AO_E8B6CC_MESSAGE_QUEUE_ITEM" USING btree ("MESSAGE_ID");


--
-- Name: index_ao_e8b6cc_pr_1045528152; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_pr_1045528152 ON "AO_E8B6CC_PR_TO_COMMIT" USING btree ("REQUEST_ID");


--
-- Name: index_ao_e8b6cc_pr_1105917040; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_pr_1105917040 ON "AO_E8B6CC_PR_PARTICIPANT" USING btree ("PULL_REQUEST_ID");


--
-- Name: index_ao_e8b6cc_pr_1458633226; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_pr_1458633226 ON "AO_E8B6CC_PR_TO_COMMIT" USING btree ("COMMIT_ID");


--
-- Name: index_ao_e8b6cc_rep1082901832; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_rep1082901832 ON "AO_E8B6CC_REPO_TO_CHANGESET" USING btree ("REPOSITORY_ID");


--
-- Name: index_ao_e8b6cc_rep922992576; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_rep922992576 ON "AO_E8B6CC_REPO_TO_CHANGESET" USING btree ("CHANGESET_ID");


--
-- Name: index_ao_e8b6cc_syn493078035; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_ao_e8b6cc_syn493078035 ON "AO_E8B6CC_SYNC_EVENT" USING btree ("REPO_ID");


--
-- Name: issue_assignee; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX issue_assignee ON jiraissue USING btree (assignee);


--
-- Name: issue_proj_num; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX issue_proj_num ON jiraissue USING btree (issuenum, project);


--
-- Name: issue_proj_status; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX issue_proj_status ON jiraissue USING btree (project, issuestatus);


--
-- Name: issue_updated; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX issue_updated ON jiraissue USING btree (updated);


--
-- Name: issue_workflow; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX issue_workflow ON jiraissue USING btree (workflow_id);


--
-- Name: issuelink_dest; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX issuelink_dest ON issuelink USING btree (destination);


--
-- Name: issuelink_src; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX issuelink_src ON issuelink USING btree (source);


--
-- Name: issuelink_type; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX issuelink_type ON issuelink USING btree (linktype);


--
-- Name: label_fieldissue; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX label_fieldissue ON label USING btree (issue, fieldid);


--
-- Name: label_fieldissuelabel; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX label_fieldissuelabel ON label USING btree (issue, fieldid, label);


--
-- Name: label_issue; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX label_issue ON label USING btree (issue);


--
-- Name: label_label; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX label_label ON label USING btree (label);


--
-- Name: licenserolegroup_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX licenserolegroup_index ON licenserolesgroup USING btree (license_role_name, group_id);


--
-- Name: linktypename; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX linktypename ON issuelinktype USING btree (linkname);


--
-- Name: linktypestyle; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX linktypestyle ON issuelinktype USING btree (pstyle);


--
-- Name: managedconfigitem_id_type_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX managedconfigitem_id_type_idx ON managedconfigurationitem USING btree (item_id, item_type);


--
-- Name: mshipbase_group; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX mshipbase_group ON membershipbase USING btree (group_name);


--
-- Name: mshipbase_user; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX mshipbase_user ON membershipbase USING btree (user_name);


--
-- Name: node_id_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX node_id_idx ON nodeindexcounter USING btree (node_id, sending_node_id);


--
-- Name: node_operation_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX node_operation_idx ON replicatedindexoperation USING btree (node_id, affected_index, operation, index_time);


--
-- Name: node_sink; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX node_sink ON nodeassociation USING btree (sink_node_id, sink_node_entity);


--
-- Name: node_source; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX node_source ON nodeassociation USING btree (source_node_id, source_node_entity);


--
-- Name: notif_source; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX notif_source ON notificationinstance USING btree (source);


--
-- Name: ntfctn_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ntfctn_scheme ON notification USING btree (scheme);


--
-- Name: oauth_consumer_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX oauth_consumer_index ON oauthconsumer USING btree (consumer_key);


--
-- Name: oauth_consumer_service_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX oauth_consumer_service_index ON oauthconsumer USING btree (consumerservice);


--
-- Name: oauth_consumer_token_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX oauth_consumer_token_index ON oauthconsumertoken USING btree (token);


--
-- Name: oauth_consumer_token_key_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX oauth_consumer_token_key_index ON oauthconsumertoken USING btree (token_key);


--
-- Name: oauth_sp_consumer_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX oauth_sp_consumer_index ON oauthspconsumer USING btree (consumer_key);


--
-- Name: oauth_sp_consumer_key_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX oauth_sp_consumer_key_index ON oauthsptoken USING btree (consumer_key);


--
-- Name: oauth_sp_token_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX oauth_sp_token_index ON oauthsptoken USING btree (token);


--
-- Name: osgroup_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX osgroup_name ON groupbase USING btree (groupname);


--
-- Name: osproperty_all; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX osproperty_all ON propertyentry USING btree (entity_id);


--
-- Name: osproperty_entityname; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX osproperty_entityname ON propertyentry USING btree (entity_name);


--
-- Name: osproperty_propertykey; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX osproperty_propertykey ON propertyentry USING btree (property_key);


--
-- Name: osuser_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX osuser_name ON userbase USING btree (username);


--
-- Name: ppage_username; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ppage_username ON portalpage USING btree (username);


--
-- Name: prmssn_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX prmssn_scheme ON schemepermissions USING btree (scheme);


--
-- Name: remembermetoken_username_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX remembermetoken_username_index ON remembermetoken USING btree (username);


--
-- Name: remotelink_globalid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX remotelink_globalid ON remotelink USING btree (globalid);


--
-- Name: remotelink_issueid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX remotelink_issueid ON remotelink USING btree (issueid, globalid);


--
-- Name: role_player_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX role_player_idx ON projectroleactor USING btree (projectroleid, pid);


--
-- Name: rundetails_jobid_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX rundetails_jobid_idx ON rundetails USING btree (job_id);


--
-- Name: rundetails_starttime_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX rundetails_starttime_idx ON rundetails USING btree (start_time);


--
-- Name: screenitem_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX screenitem_scheme ON fieldscreenschemeitem USING btree (fieldscreenscheme);


--
-- Name: searchrequest_filternamelower; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX searchrequest_filternamelower ON searchrequest USING btree (filtername_lower);


--
-- Name: sec_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX sec_scheme ON schemeissuesecurities USING btree (scheme);


--
-- Name: sec_security; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX sec_security ON schemeissuesecurities USING btree (security);


--
-- Name: share_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX share_index ON sharepermissions USING btree (entityid, entitytype);


--
-- Name: source_destination_node_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX source_destination_node_idx ON clustermessage USING btree (source_node, destination_node);


--
-- Name: sr_author; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX sr_author ON searchrequest USING btree (authorname);


--
-- Name: subscrpt_user; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX subscrpt_user ON filtersubscription USING btree (filter_i_d, username);


--
-- Name: subscrptn_group; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX subscrptn_group ON filtersubscription USING btree (filter_i_d, groupname);


--
-- Name: trustedapp_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX trustedapp_id ON trustedapp USING btree (application_id);


--
-- Name: type_key; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX type_key ON genericconfiguration USING btree (datatype, datakey);


--
-- Name: uh_type_user_entity; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uh_type_user_entity ON userhistoryitem USING btree (entitytype, username, entityid);


--
-- Name: uk_application_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uk_application_name ON cwd_application USING btree (lower_application_name);


--
-- Name: uk_directory_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX uk_directory_name ON cwd_directory USING btree (lower_directory_name);


--
-- Name: uk_group_attr_name_lval; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uk_group_attr_name_lval ON cwd_group_attributes USING btree (group_id, attribute_name, lower_attribute_value);


--
-- Name: uk_group_name_dir_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uk_group_name_dir_id ON cwd_group USING btree (lower_group_name, directory_id);


--
-- Name: uk_lower_user_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uk_lower_user_name ON app_user USING btree (lower_user_name);


--
-- Name: uk_mem_parent_child_type; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uk_mem_parent_child_type ON cwd_membership USING btree (parent_id, child_id, membership_type);


--
-- Name: uk_user_attr_name_lval; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX uk_user_attr_name_lval ON cwd_user_attributes USING btree (user_id, attribute_name);


--
-- Name: uk_user_externalid_dir_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX uk_user_externalid_dir_id ON cwd_user USING btree (external_id, directory_id);


--
-- Name: uk_user_key; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uk_user_key ON app_user USING btree (user_key);


--
-- Name: uk_user_name_dir_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uk_user_name_dir_id ON cwd_user USING btree (lower_user_name, directory_id);


--
-- Name: upf_customfield; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX upf_customfield ON userpickerfilter USING btree (customfield);


--
-- Name: upf_fieldconfigid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX upf_fieldconfigid ON userpickerfilter USING btree (customfieldconfig);


--
-- Name: user_sink; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX user_sink ON userassociation USING btree (sink_node_id, sink_node_entity);


--
-- Name: user_source; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX user_source ON userassociation USING btree (source_name);


--
-- Name: userpref_portletconfiguration; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX userpref_portletconfiguration ON gadgetuserpreference USING btree (portletconfiguration);


--
-- Name: votehistory_issue_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX votehistory_issue_index ON votehistory USING btree (issueid);


--
-- Name: wf_entryid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX wf_entryid ON os_currentstep USING btree (entry_id);


--
-- Name: workflow_scheme; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX workflow_scheme ON workflowschemeentity USING btree (scheme);


--
-- Name: worklog_author; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX worklog_author ON worklog USING btree (author);


--
-- Name: worklog_issue; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX worklog_issue ON worklog USING btree (issueid);


--
-- Name: fk_ao_563aee_activity_entity_actor_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_activity_entity_actor_id FOREIGN KEY ("ACTOR_ID") REFERENCES "AO_563AEE_ACTOR_ENTITY"("ID");


--
-- Name: fk_ao_563aee_activity_entity_icon_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_activity_entity_icon_id FOREIGN KEY ("ICON_ID") REFERENCES "AO_563AEE_MEDIA_LINK_ENTITY"("ID");


--
-- Name: fk_ao_563aee_activity_entity_object_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_activity_entity_object_id FOREIGN KEY ("OBJECT_ID") REFERENCES "AO_563AEE_OBJECT_ENTITY"("ID");


--
-- Name: fk_ao_563aee_activity_entity_target_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_activity_entity_target_id FOREIGN KEY ("TARGET_ID") REFERENCES "AO_563AEE_TARGET_ENTITY"("ID");


--
-- Name: fk_ao_563aee_object_entity_image_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_OBJECT_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_object_entity_image_id FOREIGN KEY ("IMAGE_ID") REFERENCES "AO_563AEE_MEDIA_LINK_ENTITY"("ID");


--
-- Name: fk_ao_563aee_target_entity_image_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_563AEE_TARGET_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_target_entity_image_id FOREIGN KEY ("IMAGE_ID") REFERENCES "AO_563AEE_MEDIA_LINK_ENTITY"("ID");


--
-- Name: fk_ao_e8b6cc_branch_head_mapping_repository_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_BRANCH_HEAD_MAPPING"
    ADD CONSTRAINT fk_ao_e8b6cc_branch_head_mapping_repository_id FOREIGN KEY ("REPOSITORY_ID") REFERENCES "AO_E8B6CC_REPOSITORY_MAPPING"("ID");


--
-- Name: fk_ao_e8b6cc_branch_repository_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_BRANCH"
    ADD CONSTRAINT fk_ao_e8b6cc_branch_repository_id FOREIGN KEY ("REPOSITORY_ID") REFERENCES "AO_E8B6CC_REPOSITORY_MAPPING"("ID");


--
-- Name: fk_ao_e8b6cc_git_hub_event_repository_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_GIT_HUB_EVENT"
    ADD CONSTRAINT fk_ao_e8b6cc_git_hub_event_repository_id FOREIGN KEY ("REPOSITORY_ID") REFERENCES "AO_E8B6CC_REPOSITORY_MAPPING"("ID");


--
-- Name: fk_ao_e8b6cc_issue_to_branch_branch_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_TO_BRANCH"
    ADD CONSTRAINT fk_ao_e8b6cc_issue_to_branch_branch_id FOREIGN KEY ("BRANCH_ID") REFERENCES "AO_E8B6CC_BRANCH"("ID");


--
-- Name: fk_ao_e8b6cc_issue_to_changeset_changeset_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_ISSUE_TO_CHANGESET"
    ADD CONSTRAINT fk_ao_e8b6cc_issue_to_changeset_changeset_id FOREIGN KEY ("CHANGESET_ID") REFERENCES "AO_E8B6CC_CHANGESET_MAPPING"("ID");


--
-- Name: fk_ao_e8b6cc_message_queue_item_message_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_MESSAGE_QUEUE_ITEM"
    ADD CONSTRAINT fk_ao_e8b6cc_message_queue_item_message_id FOREIGN KEY ("MESSAGE_ID") REFERENCES "AO_E8B6CC_MESSAGE"("ID");


--
-- Name: fk_ao_e8b6cc_message_tag_message_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_MESSAGE_TAG"
    ADD CONSTRAINT fk_ao_e8b6cc_message_tag_message_id FOREIGN KEY ("MESSAGE_ID") REFERENCES "AO_E8B6CC_MESSAGE"("ID");


--
-- Name: fk_ao_e8b6cc_pr_participant_pull_request_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PR_PARTICIPANT"
    ADD CONSTRAINT fk_ao_e8b6cc_pr_participant_pull_request_id FOREIGN KEY ("PULL_REQUEST_ID") REFERENCES "AO_E8B6CC_PULL_REQUEST"("ID");


--
-- Name: fk_ao_e8b6cc_pr_to_commit_commit_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PR_TO_COMMIT"
    ADD CONSTRAINT fk_ao_e8b6cc_pr_to_commit_commit_id FOREIGN KEY ("COMMIT_ID") REFERENCES "AO_E8B6CC_COMMIT"("ID");


--
-- Name: fk_ao_e8b6cc_pr_to_commit_request_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_PR_TO_COMMIT"
    ADD CONSTRAINT fk_ao_e8b6cc_pr_to_commit_request_id FOREIGN KEY ("REQUEST_ID") REFERENCES "AO_E8B6CC_PULL_REQUEST"("ID");


--
-- Name: fk_ao_e8b6cc_repo_to_changeset_changeset_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_REPO_TO_CHANGESET"
    ADD CONSTRAINT fk_ao_e8b6cc_repo_to_changeset_changeset_id FOREIGN KEY ("CHANGESET_ID") REFERENCES "AO_E8B6CC_CHANGESET_MAPPING"("ID");


--
-- Name: fk_ao_e8b6cc_repo_to_changeset_repository_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "AO_E8B6CC_REPO_TO_CHANGESET"
    ADD CONSTRAINT fk_ao_e8b6cc_repo_to_changeset_repository_id FOREIGN KEY ("REPOSITORY_ID") REFERENCES "AO_E8B6CC_REPOSITORY_MAPPING"("ID");


REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

