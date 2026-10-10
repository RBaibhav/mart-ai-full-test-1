import { z } from "zod";

const requiredText = (label) => z.string().trim().min(1, `${label} is required.`);

export const signInSchema = z.object({
  email: z.string().trim().email("Enter a valid email address."),
  password: z.string().min(8, "Password must be at least 8 characters."),
});

export const signUpSchema = signInSchema.extend({
  name: z
    .string()
    .trim()
    .min(2, "Name must be at least 2 characters.")
    .max(80, "Name is too long."),
});

export const chatMessageSchema = z.object({
  message: z
    .string()
    .trim()
    .min(1, "Enter a message.")
    .max(2000, "Message must be under 2,000 characters."),
});

export const documentUploadSchema = z.object({
  file: z.custom(
    (value) => typeof File !== "undefined" && value instanceof File,
    "Choose a PDF, Markdown, or JSON document.",
  ),
  version: requiredText("Version").max(30, "Version is too long."),
  effectiveDate: z.string().date("Enter a valid effective date."),
  category: requiredText("Category").max(100, "Category is too long."),
  region: requiredText("Region").max(100, "Region is too long."),
});

export const catalogFilterSchema = z.object({
  search: z.string().max(100, "Search query is too long.").optional(),
  priceMin: z.union([z.coerce.number().min(0, "Price cannot be negative"), z.literal("")]).optional(),
  priceMax: z.union([z.coerce.number().min(0, "Price cannot be negative"), z.literal("")]).optional(),
}).refine(data => {
  if (data.priceMin !== "" && data.priceMax !== "" && data.priceMin !== undefined && data.priceMax !== undefined) {
    return Number(data.priceMax) >= Number(data.priceMin);
  }
  return true;
}, {
  message: "Max must be ≥ Min.",
  path: ["priceMax"]
});

export function getFieldErrors(error) {
  return error.issues.reduce((errors, issue) => {
    const field = issue.path[0];
    if (field && !errors[field]) errors[field] = issue.message;
    return errors;
  }, {});
}
