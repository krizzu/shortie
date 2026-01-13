import React from "react"
import {
  AlertDialog,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { Button } from "@/components/ui/button.tsx"
import { Spinner } from "@/components/ui/spinner.tsx"

type Props = {
  visible: boolean
  onClose: () => void
  onConfirm: () => void
  title?: string | React.ReactNode
  description?: string | React.ReactNode
  cancelText?: string
  confirmText?: string
  confirmLoading?: boolean
}

export function ConfirmationAlert({
  visible,
  onClose,
  onConfirm,
  title,
  description,
  cancelText = "Cancel",
  confirmText = "Confirm",
  confirmLoading
}: Props) {
  return (
    <AlertDialog
      open={visible}
      onOpenChange={(open) => {
        if (!open) {
          onClose()
        }
      }}
    >
      <AlertDialogContent className="cursor-default">
        <AlertDialogHeader>
          {typeof title === "string" ? (
            <AlertDialogTitle>{title}</AlertDialogTitle>
          ) : (
            title
          )}
          {typeof description === "string" ? (
            <AlertDialogDescription>{description}</AlertDialogDescription>
          ) : (
            description
          )}
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel
            disabled={confirmLoading}
            onClick={() => onClose()}
          >
            {cancelText}
          </AlertDialogCancel>
          <Button
            disabled={confirmLoading}
            onClick={() => {
              onConfirm()
            }}
          >
            {confirmLoading ? <Spinner /> : confirmText}
          </Button>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
