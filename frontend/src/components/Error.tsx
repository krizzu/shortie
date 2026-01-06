type ErrorProps = {
  error: unknown
  reset?: () => void
}

export function Error({ error, reset }: ErrorProps) {
  const title = "Something went wrong"
  let message = "An unexpected error occurred."

  if (error instanceof Error) {
    message = (error as Error).message
  }

  return (
    <div
      style={{
        padding: "1.5rem",
        maxWidth: 480,
      }}
    >
      <h2 style={{ marginBottom: "0.5rem" }}>{title}</h2>
      <p style={{ marginBottom: "1rem", color: "#666" }}>{message}</p>

      {reset && (
        <button
          onClick={reset}
          style={{
            padding: "0.5rem 0.75rem",
            fontSize: "0.875rem",
            cursor: "pointer",
          }}
        >
          Try again
        </button>
      )}
    </div>
  )
}
