from openai import OpenAI
client = OpenAI()

response = client.responses.create(
  prompt={
    "id": "pmpt_691ed7d2d87c8197aea94829244af12d03db0390c49f6e3b",
    "version": "24",
    "variables": {
      "def_language": "Russian",
      "character_sex": "male",
      "character_name": "Dimas"
    }
  },
  input=[
    {
      "role": "user",
      "content": [
        {
          "type": "input_text",
          "text": "Suck"
        }
      ]
    },
    {
      "id": "msg_0739df92c3276cb70069abea6237cc8195bde0666deaf012a3",
      "role": "assistant",
      "content": [
        {
          "type": "output_text",
          "text": "{\n  \"input\": \"Suck\",\n  \"output_language\": \"Russian\",\n  \"type\": \"word\",\n  \"meanings\": [\n    {\n      \"definition\": \"To draw something into the mouth by creating a vacuum with the lips and mouth.\",\n      \"level\": \"A2\",\n      \"example_input\": \"Dimas likes to suck on a lollipop.\",\n      \"example_output\": \"Димас любит сосать леденец.\"\n    },\n    {\n      \"definition\": \"To be very bad or unpleasant (informal).\",\n      \"level\": \"B1\",\n      \"example_input\": \"Dimas said the movie sucks because it was boring.\",\n      \"example_output\": \"Димас сказал, что фильм отстой, потому что он был скучным.\"\n    },\n    {\n      \"definition\": \"To perform oral sex (vulgar, informal).\",\n      \"level\": \"C1\",\n      \"example_input\": \"Dimas told a joke about the word 'suck' in a private conversation.\",\n      \"example_output\": \"Димас рассказал шутку про слово «сосать» в приватной беседе.\"\n    }\n  ]\n}"
        }
      ]
    }
  ],
  text={
    "format": {
      "type": "json_schema",
      "name": "bilingual_meaning_entry",
      "strict": True,
      "schema": {
        "type": "object",
        "properties": {
          "input": {
            "type": "string",
            "description": "The original word or phrase in the source language."
          },
          "output_language": {
            "type": "string",
            "description": "The language for output definitions and examples."
          },
          "type": {
            "type": "string",
            "description": "Indicates whether the input is a 'phrase' or 'word'.",
            "enum": [
              "phrase",
              "word"
            ]
          },
          "meanings": {
            "type": "array",
            "description": "List of distinct meanings of the word or phrase.",
            "items": {
              "type": "object",
              "properties": {
                "definition": {
                  "type": "string",
                  "description": "Meaning or explanation of the word or phrase in English."
                },
                "level": {
                  "type": "string",
                  "description": "CEFR proficiency level for this meaning (e.g., A2, B1, B2, C1).",
                  "pattern": "^[ABC][12]$"
                },
                "example_input": {
                  "type": "string",
                  "description": "An example input (often a definition or phrase in the native language before translation)."
                },
                "example_output": {
                  "type": "string",
                  "description": "An illustrative example or translated definition for this meaning in the target language."
                }
              },
              "required": [
                "definition",
                "level",
                "example_input",
                "example_output"
              ],
              "additionalProperties": False
            }
          }
        },
        "required": [
          "input",
          "output_language",
          "type",
          "meanings"
        ],
        "additionalProperties": False
      }
    }
  },
  reasoning={},
  max_output_tokens=2048,
  store=True,
  include=["web_search_call.action.sources"]
)