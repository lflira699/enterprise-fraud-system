import {
  Card,
  CardContent,
  Typography,
} from '@mui/material'

type DashboardMetricCardProps = {
  label: string
  value: string
}

function DashboardMetricCard({
  label,
  value,
}: DashboardMetricCardProps) {
  return (
    <Card variant="outlined">
      <CardContent>
        <Typography
          component="p"
          variant="body2"
          color="text.secondary"
        >
          {label}
        </Typography>

        <Typography
          component="p"
          variant="h4"
          sx={{
            mt: 1,
          }}
        >
          {value}
        </Typography>
      </CardContent>
    </Card>
  )
}

export default DashboardMetricCard