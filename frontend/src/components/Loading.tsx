import { Spinner } from "@/components/ui/spinner.tsx"

type LoadingProps = {
  label?: string
}

export function Loading({ label = "Loading…" }: LoadingProps) {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "200px",
        fontSize: "0.95rem",
        color: "#666",
      }}
    >
      {label}
      <Spinner />
    </div>
  )
}
