// Input Normalizer (06_AI_ARCHITECTURE.md, task's "Input Normalizer"
// section). Purely mechanical -- collapses whitespace/formatting noise and
// records input type/language. It must NEVER rephrase, add qualifiers, or
// turn an assumption into a stated fact: "Police stopped me." must stay
// exactly that, not become "Police illegally stopped me." Anything beyond
// whitespace cleanup belongs in ContextExtractor (which explicitly separates
// known facts from inference), not here.
import { InputType, NormalizedInput } from "../schemas/types";

/** True for C0/C1 control characters other than tab (9) and newline (10), which the whitespace collapse below handles. */
function isStrippableControlChar(charCode: number): boolean {
  return (charCode < 32 && charCode !== 9 && charCode !== 10) || charCode === 127;
}

function stripControlChars(text: string): string {
  let result = "";
  for (let i = 0; i < text.length; i++) {
    const ch = text.charAt(i);
    if (!isStrippableControlChar(text.charCodeAt(i))) result += ch;
  }
  return result;
}

export function normalizeInput(
  rawText: string,
  inputType: InputType,
  language: string
): NormalizedInput {
  const withoutControlChars = stripControlChars(rawText);
  const collapsedWhitespace = withoutControlChars
    .replace(/[ \t]+/g, " ")
    .replace(/\n{3,}/g, "\n\n")
    .replace(/ +\n/g, "\n")
    .trim();

  return {
    text: collapsedWhitespace,
    inputType,
    language: language || "en",
    wasModified: collapsedWhitespace !== rawText,
  };
}
