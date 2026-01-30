import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function dateToUtcDateString(d: Date): string {
  return d.toISOString().slice(0, 10)
}

export function formatNumber(value: number): string {
  return Intl.NumberFormat("en").format(value)
}