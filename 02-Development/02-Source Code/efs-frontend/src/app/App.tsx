import { Box, Typography } from '@mui/material'

function App() {
  return (
    <Box
      component="main"
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      <Typography
        component="h1"
        variant="h4"
      >
        Enterprise Fraud System
      </Typography>
    </Box>
  )
}

export default App